
/* global L */

var map;
var mymap;
var layer;
var urlGeoJSON;
var geoJSON;

$(".btnNextSlide").click(function(){
    L.Util.requestAnimFrame(mymap.invalidateSize,mymap,!1,mymap._container);
});

function initGeoJSONURL(url){
    urlGeoJSON = url;
}

function setMap(){
    L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
        maxZoom: 18,
        id: 'tobiasvdp.ac4aa6b2',
        accessToken: 'pk.eyJ1IjoidG9iaWFzdmRwIiwiYSI6ImNpbGpxcTFwaTAwYjF3NGx6bWZ2bGZkcG8ifQ.DTe2IBQLNc9zQa62kD-4_g'
    }).addTo(mymap);
    requestGeoJson();  
}

   
function setGeoJson(data){
    
    geoJSON = data;
    
    
    console.log(geoJSON);
    
}

function hideRoutePreview(){
    
    if(layer != undefined){
        mymap.removeLayer(layer);
    }
    
    
    
}

function showRoutePreview(routeID){
    
    
    //zoek juiste GeoJSON
    var data;
    var i=0;
    while(i<geoJSON.length){
        console.log(routeID);
        if(geoJSON[i].properties.description == routeID){
            data = geoJSON[i];
            i=geoJSON.length;
        }
        i++;
    }
    
    
    hideRoutePreview();
    
    
    layer = L.geoJson(data, {
        
        style: function (feature) {
            var color = "black";
            return {color: color};
        },
        onEachFeature: function (feature, layer) {
            
            layer.on('click', function(e){
                                
                //functie voor aanroepen hilight in tabel
                //click event that triggers the popup and centres it on the polygon
                
                var popup = L.popup()
                .setLatLng(e.latlng)
                .setContent("<h5>Route</h5> <p> ID = "+feature.properties.description+"</p>")
                .openOn(mymap);
               
        
            });
        }
    }).addTo(mymap);    
    
}



function failedCall(data){
    Materialize.toast("Er is onmogelijk data op te halen", 4000, 'toast bottom error') // 4000 is the duration of the toast
}


function requestGeoJson(){
    $.ajax({
        url: urlGeoJSON,
        dataType: "json",
        success: setGeoJson,
        error:failedCall
    });
}


$(document).ready(function() {
    mymap = L.map('routePreview').setView([51.096434, 3.744511], 11);
    L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
        maxZoom: 18,
        id: 'tobiasvdp.ac4aa6b2',
        accessToken: 'pk.eyJ1IjoidG9iaWFzdmRwIiwiYSI6ImNpbGpxcTFwaTAwYjF3NGx6bWZ2bGZkcG8ifQ.DTe2IBQLNc9zQa62kD-4_g'
    }).addTo(mymap);
    requestGeoJson();
});




