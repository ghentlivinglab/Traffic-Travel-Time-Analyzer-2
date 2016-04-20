/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.twitter;

import com.sun.mail.iap.ByteArray;
import iii.vop2016.verkeer2.ejb.components.IRoute;
import iii.vop2016.verkeer2.ejb.helper.BeanFactory;
import iii.vop2016.verkeer2.ejb.helper.HelperFunctions;
import iii.vop2016.verkeer2.ejb.threshold.IThresholdHandler;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author Tobias
 */
@Singleton
public class TwitterHandler implements IThresholdHandler {

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
    }

    private Properties getProperties() {
        return HelperFunctions.RetrievePropertyFile(JNDILOOKUP_PROPERTYFILE, ctx, Logger.getGlobal());
    }

    @Override
    public void notify(IRoute route, long routeId, int level, int delayTriggerLevel, int difference, int delay) {
        Properties prop = getProperties();

        String message = "";
        if (difference > 0) {
            message = prop.getProperty("twittermessageup","");
        } else {
            message = prop.getProperty("twittermessagedown","");
        }

        if (message.equals("")) {
            return;
        }
        
        message = message.replace("LEVEL", "").replace("ROUTE", route.getName()).replace("DELAYMIN", delay/60+"").replace("DELAYSEC", delay%60+"");
        
        postToTwitter(message, prop);
    }

    private static void setAuthorizationHeader(HttpsURLConnection con, String consumerkey, String nonce, String signature, String signatureMathode, String unixTimestamp, String token, String version) {
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
        con.setRequestProperty("Authorization", auth.toString());
    }

    public void postToTwitter(String status, Properties prop) {
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
                beans.getLogger().log(Level.SEVERE, "Unable to post to twitter: responsecode " + responseCode);
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

}
