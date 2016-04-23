/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var mymap;
var layer;
var coords;

$.ajaxSetup({
    headers: {'Authorization': 'Basic cm9vdDpXYzdtaXVacEE2'}
});

$(document).ready(function () {
    $("#text").text("no route");
    mymap = L.map('mapid').setView([51.096434, 3.744511], 11);

    L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
        maxZoom: 18,
        id: 'tobiasvdp.ac4aa6b2',
        accessToken: 'pk.eyJ1IjoidG9iaWFzdmRwIiwiYSI6ImNpbGpxcTFwaTAwYjF3NGx6bWZ2bGZkcG8ifQ.DTe2IBQLNc9zQa62kD-4_g'
    }).addTo(mymap);

    requestGeoJson();

});

function setGeoJson(data) {
    if (layer != undefined) {
        mymap.removeLayer(layer);
    }
    coords = [];
    layer = L.geoJson(data, {
        style: function (feature) {
            return {color: "green"};
        },
        onEachFeature: function (feature, layer) {
            layer.on('click', function () {
                $("#text").text("route " + feature.properties.description);
                //functie voor aanroepen hilight in tabel
            });
            coords.push(feature.geometry.coordinates);

        }
    }).addTo(mymap);

    //invert coord
    var temp = 0;
    for (var i = 0; i < coords.length; i++) {
        for (var j = 0; j < coords[i].length; j++) {
            temp = coords[i][j][0];
            coords[i][j][0] = coords[i][j][1];
            coords[i][j][1] = temp;
        }
    }

    for (var i = 0; i < coords.length; i++) {
        var dec = L.polylineDecorator(coords[i], {
            patterns: [
                // defines a pattern of 10px-wide dashes, repeated every 20px on the line
                {offset: 0, repeat: 80, symbol: L.Symbol.arrowHead({pixelSize: 8,headAngle: 30,pathOptions: {color: "black"}})}
            ]
        }).addTo(mymap);
    }
}

function failedCall(data) {
    alert("failed to retrieve data");
}


function requestGeoJson() {
    $.ajax({
        url: "http://verkeer-2.bp.tiwi.be/api/v2/geojson/all/current",
        dataType: "json",
        success: setGeoJson,
        error: failedCall
    });
}