/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.twitter;

import iii.vop2016.verkeer2.ejb.components.IGeoLocation;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.geojson.GeoJsonRemote;
import iii.vop2016.verkeer2.ejb.geojson.IGeoJson;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import iii.vop2016.verkeer2.ejb.properties.IProperties;
import iii.vop2016.verkeer2.ejb.threshold.IThresholdHandler;
import iii.vop2016.verkeer2.ejb.threshold.ThresholdHandlerLocal;
import iii.vop2016.verkeer2.ejb.threshold.ThresholdHandlerRemote;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.AccessTimeout;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author Tobias
 */
@Singleton
@Lock(LockType.WRITE)
@AccessTimeout(value = 120000)
public class TwitterHandler implements ThresholdHandlerLocal, ThresholdHandlerRemote {

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private InitialContext ctx;
    @Resource
    private SessionContext sctx;
    protected static final String JNDILOOKUP_PROPERTYFILE = "resources/properties/Twitter";
    private static int count = 0;
    private BeanFactory beans;

    private static String generateSignature(String baseUrl, String requestMethode, Map<String, String> params, String consumerSecret, String tokenSecret) throws DecoderException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        Map<String, String> enc = new TreeMap<>();
        for (Map.Entry<String, String> param : params.entrySet()) {
            enc.put(Encoder.percentEncode(param.getKey()), Encoder.percentEncode(param.getValue()));
        }

        StringBuilder b = new StringBuilder();
        for (Map.Entry<String, String> param : enc.entrySet()) {
            b.append(param.getKey());
            b.append("=");
            b.append(param.getValue());
            b.append("&");
        }
        b.deleteCharAt(b.length() - 1);

        StringBuilder out = new StringBuilder();
        out.append(requestMethode.toUpperCase());
        out.append("&");
        out.append(Encoder.percentEncode(baseUrl));
        out.append("&");
        out.append(Encoder.percentEncode(b.toString()));

        String signingKey = Encoder.percentEncode(consumerSecret) + "&" + Encoder.percentEncode(tokenSecret);

        byte[] decodedKey = signingKey.getBytes("UTF-8");
        SecretKeySpec keySpec = new SecretKeySpec(decodedKey, HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(keySpec);
        byte[] dataBytes = out.toString().getBytes("UTF-8");
        byte[] signatureBytes = mac.doFinal(dataBytes);
        String signature = new String(Base64.encodeBase64(signatureBytes), "UTF-8");

        return signature;
    }

    @PostConstruct
    protected void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            Logger.getLogger(TwitterHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        beans = BeanFactory.getInstance(ctx, sctx);

        IProperties propCol = beans.getPropertiesCollection();
        if (propCol != null) {
            propCol.registerProperty(JNDILOOKUP_PROPERTYFILE);
        }
    }

    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }

    @Override
    public void notify(IRoute route, long routeId, int level, int delayTriggerLevel, int difference, int delay) {
        Properties prop = getProperties();

        BufferedImage img = getImageForRoute(route, prop);
        if (img != null) {

            try {
                String imageID = uploadImgToTwitter(img, prop);

                String message = "";
                if (difference > 0) {
                    message = prop.getProperty("twittermessageup", "");
                } else {
                    message = prop.getProperty("twittermessagedown", "");
                }

                if (message.equals("")) {
                    return;
                }
                String lvl = prop.getProperty("trafficlevel" + level, "");
                message = message.replace("LEVEL", lvl).replace("ROUTE", route.getName()).replace("DELAYMIN", delay / 60 + "").replace("DELAYSEC", delay % 60 + "");

                Thread.sleep(1000);

                postToTwitter(message, imageID, prop);
            } catch (InterruptedException ex) {
                Logger.getLogger(TwitterHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void setAuthorizationHeader(HttpsURLConnection con, String consumerkey, String nonce, String signature, String signatureMathode, String unixTimestamp, String token, String version) {
        con.setRequestProperty("Authorization", BuildAuthorizationHeader(consumerkey, nonce, signature, signatureMathode, unixTimestamp, token, version));
    }

    private static void setAuthorizationHeader(HttpPost con, String consumerkey, String nonce, String signature, String signatureMathode, String unixTimestamp, String token, String version) {
        con.setHeader("Authorization", BuildAuthorizationHeader(consumerkey, nonce, signature, signatureMathode, unixTimestamp, token, version));
    }

    private static String BuildAuthorizationHeader(String consumerkey, String nonce, String signature, String signatureMathode, String unixTimestamp, String token, String version) {
        StringBuilder auth = new StringBuilder();
        auth.append("OAuth oauth_consumer_key=\"");
        auth.append(Encoder.percentEncode(consumerkey));
        auth.append("\", oauth_nonce=\"");
        auth.append(Encoder.percentEncode(nonce));
        auth.append("\", oauth_signature=\"");
        auth.append(Encoder.percentEncode(signature));
        auth.append("\", oauth_signature_method=\"" + signatureMathode + "\",oauth_timestamp=\"");
        auth.append(Encoder.percentEncode(unixTimestamp));
        auth.append("\", oauth_token=\"");
        auth.append(Encoder.percentEncode(token));
        auth.append("\", oauth_version=\"" + Encoder.percentEncode(version) + "\"");
        return auth.toString();
    }

    public void postToTwitter(String status, String imageID, Properties prop) {
        try {
            Date d = new Date();

            String baseUrl = prop.getProperty("baseUrl", "");
            String requestMethode = prop.getProperty("requestMethode", "");
            String consumerkey = prop.getProperty("consumerkey", "");
            String consumerSecret = prop.getProperty("consumerSecret", "");
            String accessTokenSecret = prop.getProperty("accessTokenSecret", "");
            String nonce = new String(Base64.encodeBase64((d.toString() + count).getBytes("UTF-8")));
            String unixTimestamp = (d.getTime() / 1000) + "";
            String signatureMathode = prop.getProperty("signatureMathode", "");
            String token = prop.getProperty("token", "");
            String version = prop.getProperty("version", "");

            if (baseUrl.equals("") || requestMethode.equals("") || consumerkey.equals("") || consumerSecret.equals("") || accessTokenSecret.equals("") || nonce.equals("") || unixTimestamp.equals("") || signatureMathode.equals("") || token.equals("") || version.equals("")) {
                beans.getLogger().log(Level.SEVERE, "Invalid properties for twitter services. Check property file.");
                return;
            }

            //generate signature
            Map<String, String> param = new TreeMap<>();
            param.put("status", status);

            /*if (imageID != null) {
                param.put("media_ids", imageID);
            }*/
            param.put("oauth_consumer_key", consumerkey);
            param.put("oauth_nonce", nonce);
            param.put("oauth_signature_method", signatureMathode);
            param.put("oauth_timestamp", unixTimestamp);
            param.put("oauth_token", token);
            param.put("oauth_version", version);
            String signature = generateSignature(baseUrl, requestMethode, param, consumerSecret, accessTokenSecret);

            //open connection
            HttpsURLConnection con = null;
            URL url = new URL(baseUrl);
            con = (HttpsURLConnection) url.openConnection();

            //set post methode
            con.setRequestMethod(requestMethode);

            //set authorization header
            setAuthorizationHeader(con, consumerkey, nonce, signature, signatureMathode, unixTimestamp, token, version);

            //write body
            con.setUseCaches(false);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(("status=" + Encoder.percentEncode(status)));

            /*if (imageID != null) {
                wr.writeBytes("&media_ids=" + Encoder.percentEncode(imageID));
            }*/
            wr.close();

            //get response
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream is = con.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\n');
                }
                rd.close();
                beans.getLogger().log(Level.FINER, "Posted to twitter: " + status);
            } else {
                beans.getLogger().log(Level.SEVERE, "Unable to post to twitter: responsecode " + responseCode + " :" + con.getResponseMessage());
                InputStream is = con.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\n');
                }
                rd.close();
            }
            count++;
            if (count > 10000) {
                count = 0;
            }

        } catch (MalformedURLException ex) {
            beans.getLogger().log(Level.SEVERE, "Unable to post to twitter: MalformedURLException");
        } catch (IOException ex) {
            beans.getLogger().log(Level.SEVERE, "Unable to post to twitter: IOException");
        } catch (DecoderException | NoSuchAlgorithmException | InvalidKeyException ex) {
            beans.getLogger().log(Level.SEVERE, "Unable to post to twitter: DecoderException|NoSuchAlgorithmException|InvalidKeyException");
        }

    }

    private Map<IRoute, BufferedImage> buffer = new HashMap<>();

    private BufferedImage getImageForRoute(IRoute route, Properties prop) {
        if (buffer.containsKey(route)) {
            return buffer.get(route);
        }

        String u = prop.getProperty("imageRetrievalUrl", "");
        if (!u.equals("")) {
            try {
                IGeoJson geoBean = beans.getGeoJsonProvider();
                if (geoBean != null) {
                    List<IGeoLocation> geos = geoBean.getRoutePlotGeoLocations(route);
                    if (geos != null) {
                        //retrieve geojson
                        Map<IRoute, List<IGeoLocation>> map = new HashMap<>();
                        Map<IRoute, Integer> mapDelay = new HashMap<>();
                        map.put(route, geos);
                        mapDelay.put(route, 0);
                        String geojson = geoBean.getGeoJson(map, mapDelay);

                        //create request for img map
                        if (geojson != null && (!geojson.equals(""))) {
                            u = u.replace("GEOJSON", geojson);
                            URL url = new URL(u);
                            BufferedImage img = ImageIO.read(url);
                            if (img != null) {
                                buffer.put(route, img);
                            }
                            return img;
                        }

                    }
                }

            } catch (MalformedURLException ex) {
                beans.getLogger().log(Level.SEVERE, "Failed to retrieve the image for route " + route.getName());
            } catch (IOException ex) {
                Logger.getLogger(TwitterHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private String uploadImgToTwitter(BufferedImage img, Properties prop) {
        String u = prop.getProperty("imagePostUrl", "");
        if (!u.equals("")) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(img, "png", baos);
                byte[] arr = baos.toByteArray();

                String media = URLEncoder.encode(Base64.encodeBase64String(arr), "UTF-8");

                String mediaID = getMediaID(prop, arr);
                if (mediaID != null) {
                    if (UploadToMediaID(prop, arr, mediaID)) {
                        if (FinalizeUpload(prop, mediaID)) {
                            return mediaID;
                        }
                    } else {
                        beans.getLogger().log(Level.SEVERE, "Unable to upload media to token.");
                    }
                } else {
                    beans.getLogger().log(Level.SEVERE, "Unable to aquire upload token.");
                }

            } catch (MalformedURLException ex) {
                beans.getLogger().log(Level.SEVERE, "Unable to post to twitter: MalformedURLException");
            } catch (IOException ex) {
                beans.getLogger().log(Level.SEVERE, "Unable to post to twitter: IOException");
            } catch (DecoderException | NoSuchAlgorithmException | InvalidKeyException ex) {
                beans.getLogger().log(Level.SEVERE, "Unable to post to twitter: DecoderException|NoSuchAlgorithmException|InvalidKeyException");
            }
        }
        return null;
    }

    private String getMediaID(Properties prop, byte[] arr) throws UnsupportedEncodingException, MalformedURLException, ProtocolException, IOException, DecoderException, NoSuchAlgorithmException, InvalidKeyException {
        Date d = new Date();

        String baseUrl = prop.getProperty("imagePostUrl", "");
        String requestMethode = prop.getProperty("imagePostRequestMethode", "");
        String consumerkey = prop.getProperty("consumerkey", "");
        String consumerSecret = prop.getProperty("consumerSecret", "");
        String accessTokenSecret = prop.getProperty("accessTokenSecret", "");
        String nonce = new String(Base64.encodeBase64((d.toString() + count).getBytes("UTF-8")));
        String unixTimestamp = (d.getTime() / 1000) + "";
        String signatureMathode = prop.getProperty("signatureMathode", "");
        String token = prop.getProperty("token", "");
        String version = prop.getProperty("version", "");
        String media_type = prop.getProperty("media_type", "");

        if (baseUrl.equals("") || requestMethode.equals("") || consumerkey.equals("") || consumerSecret.equals("") || accessTokenSecret.equals("") || nonce.equals("") || unixTimestamp.equals("") || signatureMathode.equals("") || token.equals("") || version.equals("")) {
            beans.getLogger().log(Level.SEVERE, "Invalid properties for twitter services. Check property file.");
            return null;
        }

        //generate signature
        Map<String, String> param = new TreeMap<>();
        param.put("command", "INIT");
        param.put("total_bytes", arr.length + "");
        param.put("media_type", media_type);
        param.put("oauth_consumer_key", consumerkey);
        param.put("oauth_nonce", nonce);
        param.put("oauth_signature_method", signatureMathode);
        param.put("oauth_timestamp", unixTimestamp);
        param.put("oauth_token", token);
        param.put("oauth_version", version);
        String signature = generateSignature(baseUrl, requestMethode, param, consumerSecret, accessTokenSecret);

        //open connection
        HttpsURLConnection con = null;
        URL url = new URL(baseUrl);
        con = (HttpsURLConnection) url.openConnection();

        //set post methode
        con.setRequestMethod(requestMethode);

        //set authorization header
        setAuthorizationHeader(con, consumerkey, nonce, signature, signatureMathode, unixTimestamp, token, version);

        //write body
        con.setUseCaches(false);
        con.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes("command=INIT&total_bytes=" + arr.length + "&media_type=" + Encoder.percentEncode(media_type));
        wr.close();

        //get response
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_ACCEPTED) {
            JsonParser parser = Json.createParser(con.getInputStream());
            String tagName = "";
            while (parser.hasNext()) {
                JsonParser.Event event = parser.next();

                if (event == JsonParser.Event.VALUE_STRING) {
                    switch (tagName) {
                        case "media_id_string":
                            String ret = parser.getString();
                            parser.close();
                            return ret;
                    }
                }

                if (event == JsonParser.Event.KEY_NAME) {
                    tagName = parser.getString();
                } else {
                    tagName = "";
                }
            }
        } else {
            beans.getLogger().log(Level.SEVERE, "Unable to post to twitter: responsecode " + responseCode);
        }
        count++;
        if (count > 10000) {
            count = 0;
        }
        return null;
    }

    private boolean UploadToMediaID(Properties prop, byte[] arr, String media_id) throws UnsupportedEncodingException, MalformedURLException, ProtocolException, IOException, DecoderException, NoSuchAlgorithmException, InvalidKeyException {
        Date d = new Date();
        String mediaData = Base64.encodeBase64String(arr);

        String baseUrl = prop.getProperty("imagePostUrl", "");
        String requestMethode = prop.getProperty("imagePostRequestMethode", "");
        String consumerkey = prop.getProperty("consumerkey", "");
        String consumerSecret = prop.getProperty("consumerSecret", "");
        String accessTokenSecret = prop.getProperty("accessTokenSecret", "");
        String nonce = new String(Base64.encodeBase64((d.toString() + count).getBytes("UTF-8")));
        String unixTimestamp = (d.getTime() / 1000) + "";
        String signatureMathode = prop.getProperty("signatureMathode", "");
        String token = prop.getProperty("token", "");
        String version = prop.getProperty("version", "");

        if (baseUrl.equals("") || requestMethode.equals("") || consumerkey.equals("") || consumerSecret.equals("") || accessTokenSecret.equals("") || nonce.equals("") || unixTimestamp.equals("") || signatureMathode.equals("") || token.equals("") || version.equals("")) {
            beans.getLogger().log(Level.SEVERE, "Invalid properties for twitter services. Check property file.");
            return false;
        }

        //generate signature
        Map<String, String> param = new TreeMap<>();
        param.put("oauth_consumer_key", consumerkey);
        param.put("oauth_nonce", nonce);
        param.put("oauth_signature_method", signatureMathode);
        param.put("oauth_timestamp", unixTimestamp);
        param.put("oauth_token", token);
        param.put("oauth_version", version);
        String signature = generateSignature(baseUrl, requestMethode, param, consumerSecret, accessTokenSecret);

        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPost post = new HttpPost(baseUrl);

        setAuthorizationHeader(post, consumerkey, nonce, signature, signatureMathode, unixTimestamp, token, version);

        HttpEntity entity = MultipartEntityBuilder.create()
                .addPart("command", new StringBody("APPEND"))
                .addPart("media_id", new StringBody(media_id))
                .addPart("segment_index", new StringBody("0"))
                .addBinaryBody("media", arr)
                .build();

        post.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(post);

        count++;
        if (count > 10000) {
            count = 0;
        }

        //get response
        int responseCode = response.getStatusLine().getStatusCode();
        if ((responseCode / 100) == 2) {
            return true;
        }
        return false;
    }

    private boolean FinalizeUpload(Properties prop, String mediaID) throws UnsupportedEncodingException, MalformedURLException, ProtocolException, IOException, DecoderException, NoSuchAlgorithmException, InvalidKeyException {
        Date d = new Date();

        String baseUrl = prop.getProperty("imagePostUrl", "");
        String requestMethode = prop.getProperty("imagePostRequestMethode", "");
        String consumerkey = prop.getProperty("consumerkey", "");
        String consumerSecret = prop.getProperty("consumerSecret", "");
        String accessTokenSecret = prop.getProperty("accessTokenSecret", "");
        String nonce = new String(Base64.encodeBase64((d.toString() + count).getBytes("UTF-8")));
        String unixTimestamp = (d.getTime() / 1000) + "";
        String signatureMathode = prop.getProperty("signatureMathode", "");
        String token = prop.getProperty("token", "");
        String version = prop.getProperty("version", "");
        String media_type = prop.getProperty("media_type", "");

        if (baseUrl.equals("") || requestMethode.equals("") || consumerkey.equals("") || consumerSecret.equals("") || accessTokenSecret.equals("") || nonce.equals("") || unixTimestamp.equals("") || signatureMathode.equals("") || token.equals("") || version.equals("")) {
            beans.getLogger().log(Level.SEVERE, "Invalid properties for twitter services. Check property file.");
            return false;
        }

        //generate signature
        Map<String, String> param = new TreeMap<>();
        param.put("command", "FINALIZE");
        param.put("media_id", mediaID);
        param.put("oauth_consumer_key", consumerkey);
        param.put("oauth_nonce", nonce);
        param.put("oauth_signature_method", signatureMathode);
        param.put("oauth_timestamp", unixTimestamp);
        param.put("oauth_token", token);
        param.put("oauth_version", version);
        String signature = generateSignature(baseUrl, requestMethode, param, consumerSecret, accessTokenSecret);

        //open connection
        HttpsURLConnection con = null;
        URL url = new URL(baseUrl);
        con = (HttpsURLConnection) url.openConnection();

        //set post methode
        con.setRequestMethod(requestMethode);

        //set authorization header
        setAuthorizationHeader(con, consumerkey, nonce, signature, signatureMathode, unixTimestamp, token, version);

        //write body
        con.setUseCaches(false);
        con.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());

        wr.writeBytes("command=FINALIZE&media_id=" + Encoder.percentEncode(mediaID));
        wr.close();

        count++;
        if (count > 10000) {
            count = 0;
        }

        //get response
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_ACCEPTED || responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
            InputStream is = con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\n');
            }
            rd.close();

            return true;
        }
        return false;
    }

}
