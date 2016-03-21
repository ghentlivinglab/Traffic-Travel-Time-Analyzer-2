
var timerProgress = 98;
var map;
var mymap;
var layer;

function setTimerProgress(){
    timerProgress += 0.5;
    if(timerProgress%100 === 0){
        timerProgress = 0;
        refreshLiveData();
    }
    progressBar = $("#timerProgress");
    progressBar.attr("style","width:"+timerProgress+"%;");
}

function refreshLiveData(){
    Materialize.toast('De verkeerssituatie werd zonet geüpdated', 4000, 'toast bottom') // 4000 is the duration of the toast
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

function switchBtnLiveAvg(naam){
    if(naam === "live"){
        $("#btnSwitchToLive").parent().addClass("active");
        $("#btnSwitchToAvg").parent().removeClass("active");
    }
        
    if(naam === "avg"){
        $("#btnSwitchToAvg").parent().addClass("active");
        $("#btnSwitchToLive").parent().removeClass("active");
    }
        
}

function setLiveModus(){
    setLiveMap();
    setLiveList();
    switchBtnLiveAvg("live");
}

function setAvgModus(){
    setAvgMap();
    setAvgList();
    switchBtnLiveAvg("avg");
}

function setLiveList(){
    trafficList = $("#traffic-list");
    
    trafficList.html("");
    
    header = $("<ul/>").addClass("traffic-list")
            .append($("<li/>").append($("<table/>").addClass("highlight").append($("<thead/>")
            .append($("<tr/>")
            .append($("<th/>").text("Traject").attr("width","50%").attr("data-field","id"))
            .append($("<th/>").text("Reistijd").attr("width","20%").attr("data-field","duration").addClass("center"))
            .append($("<th/>").text("Vertraging").attr("width","20%").attr("data-field","delay").addClass("center"))
            .append($("<th/>").attr("width","10%"))
    ))));

    trafficList.append(header);


    for (var i = 0; i < 15; i++) {
        name = "R4: Gent - Zelzate";
        duration = "16 min";
        delay = "2 min";
        trend = "call_made";

        trafficListItem = $("<ul/>").addClass("traffic-list")
                .append($("<li/>").append($("<table/>").addClass("highlight").append($("<thead/>")
                .append($("<tr/>")
                .append($("<td/>").text(name).attr("width","50%"))
                .append($("<td/>").text(duration).attr("width","20%").addClass("center"))
                .append($("<td/>").append($("<span/>").addClass("badge slow").text(delay)).attr("width","20%").addClass("center"))
                .append($("<td/>").attr("width","10%").append($("<i/>").addClass("material-icons").text(trend)))
        ))));

        trafficList.append(trafficListItem);
    }
    Materialize.showStaggeredList('.traffic-list');
}

function setAvgList(){
    trafficList = $("#traffic-list");
    
    trafficList.html("");
    
    header = $("<ul/>").addClass("traffic-list")
            .append($("<li/>").append($("<table/>").addClass("highlight").append($("<thead/>")
            .append($("<tr/>")
            .append($("<th/>").text("Traject").attr("width","50%").attr("data-field","id"))
            .append($("<th/>").text("Reistijd").attr("width","20%").attr("data-field","duration").addClass("center"))
            .append($("<th/>").text("Vertraging").attr("width","20%").attr("data-field","delay").addClass("center"))
            .append($("<th/>").attr("width","10%"))
    ))));

    trafficList.append(header);


    for (var i = 0; i < 10; i++) {
        name = "R4: Gent - Zelzate";
        duration = "16 min";
        delay = "2 min";
        trend = "call_made";

        trafficListItem = $("<ul/>").addClass("traffic-list")
                .append($("<li/>").append($("<table/>").addClass("highlight").append($("<thead/>")
                .append($("<tr/>")
                .append($("<td/>").text(name).attr("width","50%"))
                .append($("<td/>").text(duration).attr("width","20%").addClass("center"))
                .append($("<td/>").append($("<span/>").addClass("badge slow").text(delay)).attr("width","20%").addClass("center"))
                .append($("<td/>").attr("width","10%"))
        ))));

        trafficList.append(trafficListItem);
    }
    Materialize.showStaggeredList('.traffic-list');
}



function setGeoJson(data){
    if(layer != undefined){
        mymap.removeLayer(layer);
    }
    layer = L.geoJson(data, {
        style: function (feature) {
            return {color: feature.properties.color};
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
    Materialize.toast("Er is onmogelijk data op te halen", 4000, 'toast bottom') // 4000 is the duration of the toast
}


function requestGeoJson(){
    $.ajax({
            url:"http://localhost:8080/rest/v2/geojson/all",
            dataType: "json",
            success: setGeoJson,
            error:failedCall
    });
}


$(document).ready(function() {
    
    mymap = L.map('map').setView([51.096434, 3.744511], 11);
    setLiveModus();
    initGUI();
    setInterval(setTimerProgress,1500);
    
    //$("#text").text("no route");
    

    requestGeoJson();
    
});


