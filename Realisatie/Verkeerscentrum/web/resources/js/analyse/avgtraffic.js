

/* global urlProviderComparer, Materialize, d3, colors */

var dataURL;

function initAnalyse(url){
    dataURL = url;
    $.ajax({
        url: dataURL,
        dataType: "json",
        success: viewAnalyseData,
        error: function(jqXHR, textStatus, errorThrown ){
            Materialize.toast('Er kan geen nieuwe data worden opgehaald!', 4000, 'toast bottom error');
        }
    });
}



function splitToArraySorted(obj, xdata, ydata){
    var keys = [];
    var k, i, len;
    
    for (k in obj) {
        if (obj.hasOwnProperty(k)) {
            keys.push(k);
        }
    }
    
    keys.sort();
    
    len = keys.length;
    
    for (i = 0; i < len; i++) {
        k = keys[i];
        //alert(k + ':' + obj[k]);
        xdata.push(k);
        ydata.push(obj[k]);
    }
}

function formatDuration(data) {
        var date = new Date();
        date.setTime(0);
        date.setSeconds(data);
        return dateFormat(date, "MM")+"m"+dateFormat(date, "ss");
};

function formatDuration(data) {
    var date = new Date();
    date.setTime(0);
    date.setSeconds(data);
    var min = dateFormat(date, "MM");
    var sec = dateFormat(date, "ss");
    var res = "";
    res += min + "\'";
    res += sec + "\"";
    return res;
}

function viewAnalyseData(data){
    
    
    //
    // PARSE DATA
    //
       
    
    
    
    
    
    //
    // DRAW RESULTS (TABLE)
    //
    
  
    var table = $("<table />").addClass("table striped");
    var row;;
    var col;
    
    
    //HEADER
    row = $("<tr />").addClass("header");
    col = $("<th />").text("Naam");
    row.append(col);
    col = $("<th />").addClass("center").text("Afstand");
    row.append(col);
    col = $("<th />").addClass("center").text("Optimale reistijd (min)");
    row.append(col);
    col = $("<th />").addClass("center").text("Optimale snelheid (km/h)");
    row.append(col);
    col = $("<th />").addClass("center").text("Gemiddelde reistijd (min)");
    row.append(col);
    col = $("<th />").addClass("center").text("Gemiddelde snelheid (km/h)");
    row.append(col);
    col = $("<th />").addClass("center").text("Verschil");
    row.append(col);
    
    table.append(row);
    
    
    //DATA
    
    for(k=0; k<data.length; k++){
        row = $("<tr />");
        var name = data[k].name;
        var optDuration = data[k].optimalDuration;
        var optVelocity = data[k].optimalVelocity;
        var avgDuration = data[k].avgDuration;
        var avgVelocity = data[k].avgVelocity;
        var distance = data[k].distance;
        var quotient = 0;
        var quotientClass = "";
        
        if(optDuration < avgDuration && optDuration>0){
            var quotient = Math.round((1-optDuration/avgDuration)*10000)/100;
        }
        
        if(quotient > 40){
            quotientClass = "#ff7043";
        }else if(quotient > 30){
            quotientClass = "#ff9800";
        }else if(quotient > 20){
            quotientClass = "#ffca28";
        }else if(quotient > 10){
            quotientClass = "#ffeb3b";
        }else{
            quotientClass = "#c6ff00";
        }
        
        if(optDuration > 0){
            optDuration = Math.round(optDuration/60*100)/100;
        }else{
            optDuration = "";
        }
        
        if(optVelocity > 0){
            optVelocity = Math.round(optVelocity*3.6*100)/100;
        }else{
            optVelocity = "";
        }
        
        if(avgDuration > 0){
            avgDuration = Math.round(avgDuration/60*100)/100;
        }else{
            avgDuration = "";
        }
        
        if(avgVelocity > 0){
            avgVelocity = Math.round(avgVelocity*3.6*100)/100;
        }else{
            avgVelocity = "";
        }
        
        distance = Math.round(distance/1000*100).toFixed(2)/100 + " km";
        
        
        
        col = $("<td />").text(name);
        row.append(col);        
        col = $("<td />").addClass("center").text(distance);
        row.append(col);        
        col = $("<td />").addClass("center").text(optDuration);
        row.append(col);        
        col = $("<td />").addClass("center").text(optVelocity);
        row.append(col);        
        col = $("<td />").addClass("center").text(avgDuration);
        row.append(col);        
        col = $("<td />").addClass("center").text(avgVelocity);
        row.append(col);        
        col = $("<td />").addClass("center").css("background-color",quotientClass).text(quotient+" %");
        row.append(col);        
        
        table.append(row);
    }
    
   
    $("#tableTab").html("");
    $("#tableTab").append(table);
    
    
    //
    // DRAW RESULTS (GRAPH)
    //
    // COLUMNS
    var columns = [];
    columns.push(x);
    for(var i=0; i<y.length; i++){
        columns.push(y[i]);
    }
    // COLORS
    var colorMapping = {};
    for(var i=0; i<y.length; i++){
        colorMapping[y[i][0]] = colors[i];
    }
    
    parseToMinSec = function(data){
        return formatDuration(data);
    };

    
    
}