

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

function viewAnalyseData(data){
    
    
    //
    // PARSE DATA
    //
       
    
    
    
    
    
    //
    // DRAW RESULTS (TABLE)
    //
    
  
    var table = $("<table />").addClass("table");
    var row;;
    var col;
    
    
    //HEADER
    row = $("<tr />").addClass("header");
    col = $("<th />").text("Naam");
    row.append(col);
    col = $("<th />").text("Afstand");
    row.append(col);
    col = $("<th />").text("Optimale reistijd");
    row.append(col);
    col = $("<th />").text("Optimale snelheid");
    row.append(col);
    col = $("<th />").text("Gemiddelde reistijd");
    row.append(col);
    col = $("<th />").text("Gemiddelde snelheid");
    row.append(col);
    col = $("<th />").text("Verschil");
    row.append(col);
    
    table.append(row);
    
    
    //DATA
    
    for(k=0; k<data.length; k++){
        row = $("<tr />");
        var name = data[k].name;
        var optDuration = data[k].name;
        var optDuration = data[k].optimalDuration;
        var optVelocity = data[k].optimalVelocity;
        var avgDuration = data[k].avgDuration;
        var avgVelocity = data[k].avgVelocity;
        var distance = data[k].distance;
        
        col = $("<td />").text(name);
        row.append(col);        
        col = $("<td />").text(distance);
        row.append(col);        
        col = $("<td />").text(optDuration);
        row.append(col);        
        col = $("<td />").text(optVelocity);
        row.append(col);        
        col = $("<td />").text(avgDuration);
        row.append(col);        
        col = $("<td />").text(avgVelocity);
        row.append(col);        
        col = $("<td />").text("4 %");
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