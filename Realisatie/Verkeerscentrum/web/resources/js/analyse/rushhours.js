

/* global urlProviderComparer, Materialize, d3 */

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

function parseDayData(name, data){
    var y3 = [];
    y3[0] = name;
    
    console.log(data);
    
    for (i=0; i<data.length; i++) {
        if (data[i] < 0) {
            y3.push(null);
        }else{
            y3.push(data[i]);
        }
    }
    
    return y3;
}

function parseXData(name, data){
    var y3 = [];
    y3[0] = name;
    
    var day = 1;
    for (i=0; i<data.length; i++) {
        var str = data[i];
        var res = str.split(":");
        var h = res[0];
        var m = res[1];
        console.log(res[0],res[1]);
        var date = new Date();
        date.setHours(h);
        date.setMinutes(m);
        date.setDate(day);
        if(h==23 && m==45) day++;        
        y3.push(date);
    }
    
    return y3;
}

function viewAnalyseData(data){
    
    var route = data[0];
   
    //
    // PARSE DATA
    //
    var x = [];
    var y = [];
    
    //
    // Y - AX
    //
    var y2 = [];
    y2 = parseDayData("workday",route.data.duration.data.workday);
    y.push(y2);
        
    console.log(y2);
        
    //
    // X - AX
    //
    x = parseXData("x",route.data.duration.data["x-ax"]);
    
    console.log(x);
    
        
    /*
    //
    // DRAW RESULTS (TABLE)
    //
    var divOuter = $("<div />").addClass("outer");
    var divInner = $("<div />").addClass("inner");
    
    var table = $("<table />").addClass("table");
    var row;;
    var col;
    
    
    //HEADER
    row = $("<tr />").addClass("header");
    col = $("<th />").text("").addClass("pull-bottom th");
    row.append(col);
    for(i=1; i<x.length; i++){
        col = $("<td />").addClass("center").text(x[i].format("dd/mm")+"\n"+x[i].format("HH:MM")); 
        row.append(col);
    }
    table.append(row);
    
    
    //DATA
    for(k=0; k<y.length; k++){
        row = $("<tr />");
        var providerName = y[k][0];
        col = $("<th />").addClass("th").text(providerName);
        row.append(col);
        for(i=1; i<y[k].length; i++){
            ydata = y[k][i];
            if(ydata == null){
                col = $("<td />").addClass("center").text("/");
            }else{
                var d = formatDuration(ydata);
                col = $("<td />").addClass("center").text(d);
            }
            row.append(col);  
        }
        table.append(row);
    }
    
    $("#tableTab").html("");
    divInner.append(table);
    divOuter.append(divInner);
    $("#tableTab").append(divOuter);
    $("#tableTab").addClass("scrollable horizontal");
    */
    
    
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
        var date = new Date();
        date.setTime(0);
        date.setSeconds(data);
        return dateFormat(date, "MM")+"m"+dateFormat(date, "ss");
    };

    
    chart = c3.generate({
        bindto: '#chart',
        data: {
            x: 'x',
            columns: columns,
            colors: colorMapping
        },
        grid: {
            y: {
                show: true,
                ticks: 5
            }
        },
        axis: {
            x: {
                show: true,
                type: 'timeseries',
                tick: {
                    format: '%H:%M',
                    culling: {
                        max: 7 // the number of tick texts will be adjusted to less than this value
                    }
                },
                label: {// ADD
                    text: 'Tijdstip',
                    position: 'outer-center'
                }
            },
            y: {
                type: 'number',
                tick: {
                    format: parseToMinSec,
                    culling: 2// for some reason this doesn't work
                },
                label: {// ADD
                    text: 'Reistijd',
                    position: 'outer-middle'
                }
            }
        },
        tooltip: {
            format: {
                value: function (data) {

                        var date = new Date();
                        date.setTime(0);
                        date.setSeconds(data);
                        return dateFormat(date, "MM")+"m"+dateFormat(date, "ss");
                    
                }
//            value: d3.format(',') // apply this format to both y and y2
            }
        },
        subchart: {
            show: true
        },
        zoom: {
            enabled: true
        }
    });
    
}