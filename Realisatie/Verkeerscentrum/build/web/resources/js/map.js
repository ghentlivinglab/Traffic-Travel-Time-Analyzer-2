
/* global Materialize, L */

var timerProgress = 98;

var map;
var mymap;
var layer;
var trafficData = [];
var modus = "live";
var urlGeoJSON = "http://localhost:8080/RestApi/v2/geojson/all/current";
var urlAllRoutes = "http://localhost:8080/RestApi/v2/routes/all";
var urlTimerNewData = "http://localhost:8080/RestApi/v2/timers/newdata";

/*
trafficData = [
   {
      distance:14685,
      trend:0,
      currentDelayLevel:0,
      optimalDuration:734,
      avgDuration:734,
      currentVelocity:19,
      recentData:{
         "duration":{
            "name":"TrendDurations 1",
            "description":"This data are the durations over the last hour for route 1",
            "x-ax":[

            ],
            "y-ax":[

            ]
         }
      },
      avgVelocity:2,
      optimalVelocity:2,
      name:"R4 Gent - Zelzate",
      currentDuration:753,
      id:1,
      geolocations:[
         {
            latitude:51.192226,
            name:"Zelzate",
            longitude:3.776342
         },
         {
            latitude:51.086447,
            name:"Gent",
            longitude:3.672188
         }
      ]
   }
];
*/

function setTimerProgress(){
    timerProgress += 0.5;
    if(timerProgress%100 === 0){
        timerProgress = 0;
        refreshLiveData();
    }
    progressBar = $("#timerProgress");
    progressBar.attr("style","width:"+timerProgress+"%;");
}

function initTimer(data){
    console.log(data);
    var full = data.interval;
    var currentTimeTimer = data.time;
    var currentTime = (new Date()).getTime();
    //alert("Interval timer = "+full+", al gedaan = "+(currentTime-currentTimeTimer)%1000);
    setInterval(setTimerProgress,1500);
}

function refreshLiveData(){
    
    $.ajax({
        url: urlAllRoutes,
        dataType: "json",
        success: function(data, textStatus, jqXHR ){
            Materialize.toast('De verkeerssituatie werd zonet geüpdated', 4000, 'toast bottom success');
            trafficData = data;
            setModus(modus);
        },
        error: function(jqXHR, textStatus, errorThrown ){
            Materialize.toast('Er kan geen nieuwe data worden opgehaald!', 4000, 'toast bottom error');
        }
    });
}

function initMap(){
    /*map = new google.maps.Map(document.getElementById('map'), {
          center: {lat: 51.038901, lng: 3.725215},
          scrollwheel: false,
          navigationControl: false,
          mapTypeControl: false,
          scaleControl: false,
          mapTypeId: google.maps.MapTypeId.ROADMAP,
          zoom: 15
    });
    initGUI();
    */
}

function initGUI(){
    liveTab = $("<li/>").append($("<a/>").attr("href","#!").text("Live").click(setLiveModus).attr("id","btnSwitchToLive")).addClass("active");
    avgTab = $("<li/>").append($("<a/>").attr("href","#!").text("Gemiddeld").click(setAvgModus).attr("id","btnSwitchToAvg"));
    
    nav = $("<ul/>").addClass("pagination");
    nav.append(liveTab).append(avgTab);
    mapSwitch = $("<div/>").addClass("mapSwitch").append(nav);
    $("#map").append(mapSwitch);
}


function setLiveMap(){
    //teken Live data
    L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
        maxZoom: 18,
        id: 'tobiasvdp.ac4aa6b2',
        accessToken: 'pk.eyJ1IjoidG9iaWFzdmRwIiwiYSI6ImNpbGpxcTFwaTAwYjF3NGx6bWZ2bGZkcG8ifQ.DTe2IBQLNc9zQa62kD-4_g'
    }).addTo(mymap);
}

function setAvgMap(){
    //teken Avg data
}

function switchBtnModus(){
    if(modus === "live"){
        $("#btnSwitchToLive").parent().addClass("active");
        $("#btnSwitchToAvg").parent().removeClass("active");
    }
        
    if(modus === "avg"){
        $("#btnSwitchToAvg").parent().addClass("active");
        $("#btnSwitchToLive").parent().removeClass("active");
    } 
}


function setModus(modus){
    if(modus === "live")
        setLiveModus();
    if(modus === "avg")
        setAvgModus();
}

function setLiveModus(){
    modus = "live";
    setLiveMap();
    setLiveList();
    switchBtnModus();
}

function setAvgModus(){
    modus = "avg";
    setAvgMap();
    setAvgList();
    switchBtnModus();
}

function setLiveList(){
    trafficListBox = $("#traffic-list");
    trafficListBox.html("");
    
    header = $("<ul/>").addClass("traffic-list")
            .append($("<li/>").append($("<table/>").addClass("highlight").append($("<thead/>")
            .append($("<tr/>")
            .append($("<th/>").text("Traject").attr("width","50%").attr("data-field","id"))
            .append($("<th/>").text("Reistijd").attr("width","20%").attr("data-field","duration").addClass("center"))
            .append($("<th/>").text("Vertraging").attr("width","20%").attr("data-field","delay").addClass("center"))
            .append($("<th/>").attr("width","10%"))
    ))));
    trafficListBox.append(header);
    
    trafficList = $(".traffic-list");
    if(trafficData.length>0){
        for (var i = 0; i < trafficData.length; i++) {
            var duration = {"min":0,"sec":0};
            var delay = {"min":0,"sec":0};
            var id, name, delay, trend, delayClass, delayTxt, durationTxt;
            id = trafficData[i].id;
            name = trafficData[i].name;
            duration.min = Math.round((trafficData[i].currentDuration)/60);
            duration.sec = (trafficData[i].currentDuration-duration.min);
            durationTxt = duration.min+" min";
            if(trafficData[i].optimalDuration >= 0){
                delay.sec = trafficData[i].currentDuration - trafficData[i].optimalDuration;
                delay.min = Math.round(delay.sec/60);
                delayTxt = delay.min+" min";
            }else{
                delayTxt = "? min";
            }
            if(trafficData[i].trend < 0){
                trend = "call_received";
            }else if(trafficData[i].trend > 0){
                trend = "call_made";
            }else{
                trend = "";
            }
            trend = "call_made";
            
            delayClass = "default";
            switch(trafficData[i].currentDelayLevel){
                case 0: delayClass = "veryfast"; break;
                case 1: delayClass = "fast"; break;
                case 2: delayClass = "intermediate"; break;
                case 3: delayClass = "slow"; break;
                case 4: delayClass = "verslow"; break;
            }

            trafficListItem = $("<li/>").append($("<table/>").addClass("highlight").append($("<thead/>")
                    .append($("<tr/>")
                    .append($("<td/>").text(name).attr("width","50%"))
                    .append($("<td/>").text(durationTxt).attr("width","20%").addClass("center"))
                    .append($("<td/>").append($("<span/>").addClass("badge "+delayClass).text(delayTxt)).attr("width","20%").addClass("center"))
                    .append($("<td/>").attr("width","10%").append($("<i/>").addClass("material-icons").text(trend)))
            )));
            trafficList.append(trafficListItem);
        }
    }else{
        trafficListItem = $("<li/>").text("Geen trajecten om weer te geven...");
        trafficList.append(trafficListItem);
    }
    Materialize.showStaggeredList('.traffic-list');
}

function setAvgList(){
    trafficListBox = $("#traffic-list");
    trafficListBox.html("");
    
    header = $("<ul/>").addClass("traffic-list")
            .append($("<li/>").append($("<table/>").addClass("highlight").append($("<thead/>")
            .append($("<tr/>")
            .append($("<th/>").text("Traject").attr("width","50%").attr("data-field","id"))
            .append($("<th/>").text("Reistijd").attr("width","20%").attr("data-field","duration").addClass("center"))
            .append($("<th/>").text("Vertraging").attr("width","20%").attr("data-field","delay").addClass("center"))
            .append($("<th/>").attr("width","10%"))
    ))));

    trafficListBox.append(header);

    trafficList = $(".traffic-list");
    if(trafficData.length>0){
        for (var i = 0; i < trafficData.length; i++) {
            id = trafficData[i].id;
            name = trafficData[i].name;
            duration = "8 min";
            delay = "1 min";

            trafficListItem = $("<li/>").append($("<table/>").addClass("highlight").append($("<thead/>")
                    .append($("<tr/>")
                    .append($("<td/>").text(name).attr("width","50%"))
                    .append($("<td/>").text(duration).attr("width","20%").addClass("center"))
                    .append($("<td/>").append($("<span/>").addClass("badge slow").text(delay)).attr("width","20%").addClass("center"))
                    .append($("<td/>").attr("width","10%"))
            )));
            trafficList.append(trafficListItem);
        }
    }else{
        trafficListItem = $("<li/>").text("Geen trajecten om weer te geven...");
        trafficList.append(trafficListItem);
    }
    Materialize.showStaggeredList('.traffic-list');
}



function setGeoJson(data){
    console.log(data);
    
    
    if(layer != undefined){
        mymap.removeLayer(layer);
    }
    layer = L.geoJson(data, {
        style: function (feature) {
            var color;
            switch(feature.properties.currentDelayLevel){
                case 0: color = "green"; break;
                case 1: color = "green"; break;
                case 2: color = "orange"; break;
                case 3: color = "red"; break;
                case 4: color = "black"; break;
            }
            return {color: color};
        },
        onEachFeature: function (feature, layer) {
            layer.on('click', function(){
                $("#text").text("route " + feature.properties.description);
                //functie voor aanroepen hilight in tabel
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
    mymap = L.map('map').setView([51.096434, 3.744511], 11);
    setModus("live");
    initGUI();
    //$("#text").text("no route");
    refreshLiveData();
    requestGeoJson();
    
    $.ajax({
        url: urlTimerNewData,
        dataType: "json",
        success: initTimer,
        error: function(jqXHR, textStatus, errorThrown ){
            Materialize.toast('Er kan geen data worden opgehaald over de timer op de server!', 4000, 'toast bottom error');
        }
    });
    
});


