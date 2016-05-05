

/* global Materialize, L */

var timerProgress = 0;
var mymap;
var trafficData = [];
var modus = "live";
var urlTimerNewData;
var urlAllRoutes;
var urlGeoJSONcurrent;
var geojsonLive;
var geojsonAvg;
var coords;
var decoratorGroup;
var geojsonGroup;
var boolDrawDirection;
var hilightID = -1;
var arrowsEnabled = true;


function initTimerURL(url) {
    urlTimerNewData = url;
}

function initRoutesURL(url) {
    urlAllRoutes = url;
}

function initGeoJSONURLcurrent(url) {
    urlGeoJSONcurrent = url;
}

function initGeoJSONURLavg(url) {
    urlGeoJSONavg = url;
}

function startLive() {
    setTimerData();
    setLiveModus();
    refreshLiveData();
}

function setTimerData() {
    if (urlTimerNewData) {
        $.ajax({
            url: urlTimerNewData,
            dataType: "json",
            success: initTimer,
            error: function (jqXHR, textStatus, errorThrown) {
                Materialize.toast('Er kan geen data worden opgehaald over de timer op de server!', 4000, 'toast bottom error');
            }
        });
    } else {
        Materialize.toast('Het systeem is niet klaar om te worden gestart!', 4000, 'toast bottom error');
    }
}

function setTimerProgress() {
    timerProgress += 0.5;
    if (timerProgress % 100 === 0) {
        timerProgress = 0;
        refreshLiveData();
    }
    progressBar = $("#timerProgress");
    progressBar.attr("style", "width:" + timerProgress + "%;");
}

function initTimer(data) {
    var interval = Math.floor(data.interval / 200 * 1000);
    setInterval(setTimerProgress, interval);
    timerProgress = data.percentDone;
}

function getRoute(id) {
    var i = 0;
    while (i < trafficData.length) {
        if (trafficData[i].id == id)
            return trafficData[i];
        i++;
    }
}



function refreshLiveData() {
    if (urlAllRoutes && urlGeoJSONcurrent) {
        $.ajax({
            url: urlAllRoutes,
            dataType: "json",
            success: function (data, textStatus, jqXHR) {
                Materialize.toast('De verkeerssituatie werd zonet geüpdatet', 4000, 'toast bottom success');
                trafficData = data;
                requestGeoJson();
                drawList();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Materialize.toast('Er kan geen nieuwe data worden opgehaald!', 4000, 'toast bottom error');
                trafficList = $(".traffic-list");
                trafficListItem = $("<li/>").text("Geen trajecten om weer te geven...");
                trafficList.add(trafficListItem);
                removeLayerFromMap();
            }
        });
    }
}

function initGUI() {
    liveTab = $("<li/>").append($("<a/>").attr("href", "#!").text("Live").click(setLiveModus).click(drawCurrentModus).attr("id", "btnSwitchToLive")).addClass("active");
    avgTab = $("<li/>").append($("<a/>").attr("href", "#!").text("Gemiddeld").click(setAvgModus).click(drawCurrentModus).attr("id", "btnSwitchToAvg"));

    nav = $("<ul/>").addClass("pagination");
    nav.append(liveTab).append(avgTab);
    mapSwitch = $("<div/>").addClass("mapSwitch").append(nav);
    $("#map").append(mapSwitch);


    var scale = $("<ul />");
    var scale0 = $("<li />").addClass("veryfast");
    /*var btnColor = $("<a />");
     btnColor.attr("href","#!");
     btnColor.addClass("tooltipped");
     btnColor.attr("data-position","top");
     btnColor.attr("data-delay","50");
     btnColor.attr("data-tooltip","I am tooltip");
     scale0.append(btnColor);
     */
    var scale1 = $("<li />").addClass("fast");
    var scale2 = $("<li />").addClass("intermediate");
    var scale3 = $("<li />").addClass("slow");
    var scale4 = $("<li />").addClass("veryslow");
    scale.append(scale0);
    scale.append(scale1);
    scale.append(scale2);
    scale.append(scale3);
    scale.append(scale4);


    trafficColorSwitch = $("<div/>").addClass("trafficColorScale").append(scale);
    $("#map").append(trafficColorSwitch);


}


function setLiveMap() {
    drawGeoJSON(geojsonLive);
}

function setAvgMap() {
    drawGeoJSON(geojsonAvg);
}

function switchBtnModus() {
    if (modus === "live") {
        $("#btnSwitchToLive").parent().addClass("active");
        $("#btnSwitchToAvg").parent().removeClass("active");
    }

    if (modus === "avg") {
        $("#btnSwitchToAvg").parent().addClass("active");
        $("#btnSwitchToLive").parent().removeClass("active");
    }
}


function setLiveModus() {
    modus = "live";
    switchBtnModus();
}

function setAvgModus() {
    modus = "avg";
    switchBtnModus();
}

function formatDuration(data) {
    if (data > 0) {
        var date = new Date();
        date.setTime(0);
        date.setSeconds(data);
        var min = dateFormat(date, "MM");
        var sec = dateFormat(date, "ss");
        var res = "";
        res += min + "\"";
        res += sec + "\'";
        return res;
    } else {
        return "00:00";
    }
}
;

function splitToArraySorted(obj, xdata, ydata) {
    var keys = [];
    var k, i, len;

    for (k in obj) {
        if (obj.hasOwnProperty(k)) {
            keys.push(k);
        }
    }

    keys.sort(function (a, b) {
        return b - a;
    });

    len = keys.length;

    for (i = 0; i < len; i++) {
        k = keys[i];
        xdata.push(k);
        ydata.push(obj[k]);
    }
}

function getDelayClass(delayLevel) {
    var delayClass = "default";
    switch (delayLevel) {
        case 0:
            delayClass = "veryfast";
            break;
        case 1:
            delayClass = "fast";
            break;
        case 2:
            delayClass = "intermediate";
            break;
        case 3:
            delayClass = "slow";
            break;
        case 4:
            delayClass = "veryslow";
            break;
    }
    if (delayLevel >= 4)
        delayClass = "veryslow";
    if (delayLevel <= 0)
        delayClass = "veryfast";
    return delayClass;
}

function setLiveList() {

    var data = {};
    for (var i = 0; i < trafficData.length; i++) {
        var cDuration = trafficData[i].currentDuration;
        var oDuration = trafficData[i].optimalDuration;
        var id = trafficData[i].id;
        var delay = cDuration - oDuration;
        if (oDuration >= 0 && delay > 0) {
            data[id] = delay;
        } else {
            data[id] = -1;
        }
    }
    var sortable = [];
    for (var routeid in data)
        sortable.push([routeid, data[routeid]]);
    sortable.sort(function (a, b) {
        return b[1] - a[1];
    });
    var keys = [];
    for (var i = 0; i < sortable.length; i++) {
        keys.push(sortable[i][0]);
    }

    trafficListBox = $("#traffic-list");
    trafficListBox.html("");

    header = $("<ul/>").addClass("traffic-list")
            .append($("<li/>").append($("<table/>").addClass("highlight").append($("<thead/>")
                    .append($("<tr/>")
                            .append($("<th/>").text("Traject").attr("width", "50%").attr("data-field", "id"))
                            .append($("<th/>").text("Reistijd").attr("width", "20%").attr("data-field", "duration").addClass("center"))
                            .append($("<th/>").text("Vertraging").attr("width", "20%").attr("data-field", "delay").addClass("center"))
                            .append($("<th/>").attr("width", "10%"))
                            ))));
    trafficListBox.append(header);

    trafficList = $(".traffic-list");
    var trend;
    var delayClass;
    if (trafficData.length > 0) {
        for (var i = 0; i < keys.length; i++) {
            var route = getRoute(keys[i]);
            var cDuration = route.currentDuration;
            var oDuration = route.optimalDuration;
            var delay = 0;
            var id = route.id;
            var name = route.name;
            durationTxt = formatDuration(cDuration);

            if (oDuration >= 0) {
                delay = cDuration - oDuration;
                delayTxt = formatDuration(delay);
                if (route.trend < 0) {
                    trend = "call_received";
                }
                if (route.trend > 0) {
                    trend = "call_made";
                }
                delayClass = getDelayClass(route.currentDelayLevel);
            } else {
                delayTxt = "? min";
                delayClass = "default";
            }

            var delayButton = "";
            if (delay > 0) {
                delayButton = $("<span/>").addClass("badge " + delayClass).text(delayTxt);
            } else {
                delay = 0;
            }

            trafficListItem = $("<li/>").mouseenter(routeListItemMouseEnter).mouseleave(routeListItemMouseLeave).attr("id", "route" + id).attr("routeid", id).append($("<table/>").addClass("highlight").append($("<thead/>")
                    .append($("<tr/>")
                            .append($("<td/>").text(name).attr("width", "50%"))
                            .append($("<td/>").text(durationTxt).attr("width", "20%").addClass("center"))
                            .append($("<td/>").append(delayButton).attr("width", "20%").addClass("center"))
                            .append($("<td/>").attr("width", "10%").append($("<i/>").addClass("material-icons").text(trend)))
                            )));
            trafficList.append(trafficListItem);
        }

    } else {
        trafficListItem = $("<li/>").text("Geen trajecten om weer te geven...");
        trafficList.append(trafficListItem);
    }
    Materialize.showStaggeredList('.traffic-list');
}

function setAvgList() {
    trafficListBox = $("#traffic-list");
    trafficListBox.html("");

    header = $("<ul/>").addClass("traffic-list")
            .append($("<li/>").append($("<table/>").addClass("highlight").append($("<thead/>")
                    .append($("<tr/>")
                            .append($("<th/>").text("Traject").attr("width", "60%").attr("data-field", "id"))
                            .append($("<th/>").text("Reistijd").attr("width", "20%").attr("data-field", "duration").addClass("center"))
                            .append($("<th/>").text("Vertraging").attr("width", "20%").attr("data-field", "delay").addClass("center"))
                            ))));

    trafficListBox.append(header);

    trafficList = $(".traffic-list");

    var trend;
    var delayClass = "default";
    if (trafficData.length > 0) {
        for (var i = 0; i < trafficData.length; i++) {
            var route = trafficData[i];
            var aDuration = route.avgDuration;
            var oDuration = route.optimalDuration;
            var id = route.id;
            var name = route.name;
            var delay;
            durationTxt = formatDuration(aDuration);
            if (oDuration >= 0) {
                delay = aDuration - oDuration;
                delayTxt = formatDuration(delay);
            } else {
                delayTxt = "? min";
            }
            delayClass = getDelayClass(route.currentDelayLevel);

            trafficListItem = $("<li/>").attr("id", "route" + id).append($("<table/>").addClass("highlight").append($("<thead/>")
                    .append($("<tr/>")
                            .append($("<td/>").text(name).attr("width", "60%"))
                            .append($("<td/>").text(durationTxt).attr("width", "20%").addClass("center"))
                            .append($("<td/>").append($("<span/>").addClass("badge " + delayClass).text(delayTxt)).attr("width", "20%").addClass("center"))
                            )));
            trafficList.append(trafficListItem);
        }
    } else {
        trafficListItem = $("<li/>").text("Geen trajecten om weer te geven...");
        trafficList.append(trafficListItem);
    }
    Materialize.showStaggeredList('.traffic-list');
}

function highlightRouteInList(routeid) {
    $("#traffic-list ul li").removeClass("active");
    $("#route" + routeid).addClass("active");
    $('.sidebar').animate({
        scrollTop: $('#traffic-list #route' + routeid).offset().top()
    }, 'slow');
}

function highlightRouteOnMap(routeid) {
    //remove layer
    //draw layer with route in other color and thick
    drawMap();
}

function routeListItemMouseEnter(sender) {
    $(this).addClass("active");
    hilightID = sender.currentTarget.attributes.routeid.value;
    drawMap();
}

function routeListItemMouseLeave() {
    $(this).removeClass("active");
    hilightID = -1;
    drawMap();
}

function removeLayerFromMap() {
    clearGeojson();
    clearArrows();
}

function setGeoJsonCurrent(data) {
    geojsonLive = data;
    if (modus == "live")
        drawMap();
}

function setGeoJsonAvg(data) {
    geojsonAvg = data;
    if (modus == "avg")
        drawMap();
}

function drawGeoJSON(data) {
    removeLayerFromMap();

    console.log("drawGeoJSON");

    var localCoords = [];
    var layer = L.geoJson(data, {
        style: function (feature) {
            var colorClass = getDelayClass(feature.properties.currentDelayLevel);
            var color = $(".trafficColorScale ." + colorClass).css("background-color");

            if (feature.properties.id == hilightID) {
                color = "black";
                return {color: color, opacity: 1}
            }

            if (hilightID != -1) {
                return {color: color, opacity: 0.3}
            } else {
                return {color: color};
            }
        },
        onEachFeature: function (feature, layer) {

            layer.on('click', function (e) {
                //functie voor aanroepen hilight in tabel
                //click event that triggers the popup and centres it on the polygon

                var popup = L.popup()
                        .setLatLng(e.latlng)
                        .setContent("<h5>Route</h5> <p> ID = " + feature.properties.id + "</p>")
                        .openOn(mymap);

                highlightRouteInList(feature.properties.id);

                click = true;
            });
            localCoords.push(feature.geometry.coordinates);
        }
    });
    geojsonGroup.addLayer(layer);

    //invert coord
    coords = [];
    var temp = 0;
    for (var i = 0; i < localCoords.length; i++) {
        coords.push([]);
        for (var j = 0; j < localCoords[i].length; j++) {
            x1 = localCoords[i][j][0];
            x0 = localCoords[i][j][1];

            var arr = [x0, x1];
            coords[i].push(arr);
        }
    }


    if (boolDrawDirection) {
        drawArrows();
    }


    console.log("einde draw geo json");
}

function drawArrows() {
    if (arrowsEnabled == true) {
        for (var i = 0; i < coords.length; i++) {
            var dec = L.polylineDecorator(coords[i], {
                patterns: [
                    // defines a pattern of 10px-wide dashes, repeated every 20px on the line
                    {offset: 0, repeat: 80, symbol: L.Symbol.arrowHead({pixelSize: 8, headAngle: 30, pathOptions: {color: "black"}})}
                ]
            });

            decoratorGroup.addLayer(dec);
        }
    }
}

function clearGeojson() {
    geojsonGroup.clearLayers();
}

function clearArrows() {
    decoratorGroup.clearLayers();
}


function failedCall(data) {
    Materialize.toast("Er is onmogelijk data op te halen", 4000, 'toast bottom error') // 4000 is the duration of the toast
}


function requestGeoJson() {
    $.ajax({
        url: urlGeoJSONcurrent,
        dataType: "json",
        success: setGeoJsonCurrent,
        error: failedCall
    });
    $.ajax({
        url: urlGeoJSONavg,
        dataType: "json",
        success: setGeoJsonAvg,
        error: failedCall
    });
}

function drawCurrentModus() {
    drawList();
    drawMap();
}

function drawMap() {
    if (modus == "live") {
        setLiveMap();
    }
    if (modus == "avg") {
        setAvgMap();
    }
}

function drawList() {
    if (modus == "live") {
        setLiveList();
    }
    if (modus == "avg") {
        setAvgList();
    }
}

$(document).ready(function () {
    mymap = L.map('map').setView([51.096434, 3.744511], 11);
    L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
        maxZoom: 18,
        id: 'tobiasvdp.ac4aa6b2',
        accessToken: 'pk.eyJ1IjoidG9iaWFzdmRwIiwiYSI6ImNpbGpxcTFwaTAwYjF3NGx6bWZ2bGZkcG8ifQ.DTe2IBQLNc9zQa62kD-4_g'
    }).addTo(mymap);

    decoratorGroup = L.layerGroup();
    decoratorGroup.addTo(mymap);

    geojsonGroup = L.layerGroup();
    geojsonGroup.addTo(mymap);

    boolDrawDirection = false;
    hilightID = -1;

    mymap.on('zoomend', function (event) {
        if (event.target._zoom >= 14) {
            if (boolDrawDirection == false) {
                boolDrawDirection = true;
                drawArrows();
            }
        } else {
            if (boolDrawDirection == true) {
                boolDrawDirection = false;
                clearArrows();
            }
        }
    });

    initGUI();
    startLive();
});




