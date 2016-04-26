

/* global urlProviderComparer, Materialize, d3, c3 */

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




function formatDuration(data) {
        var date = new Date();
        date.setTime(0);
        date.setSeconds(data);
        return dateFormat(date, "MM")+"m"+dateFormat(date, "ss");
};

function viewAnalyseData(data){
    
    var route = data[0];
    
    //
    // PARSE DATA
    //
    var x = [];
    var y = [];
    for(var i=0; i<route.data.length; i++){
        var durationData = route.data[i].data.duration.data;
        var xdata = [];
        var ydata = [];
        splitToArraySorted(durationData, xdata, ydata);
        //
        // X - AX
        //
        x = [];
        x[0] = 'x';
        for (var k=0; k<xdata.length; k++) {
            x[parseInt(k) + 1] = new Date(new Number(xdata[k]));
        }
        //
        // Y - AX
        //
        var y2 = [];
        y2[0] = route.data[i].provider;
        for (var k=0; k<ydata.length; k++) {
            if(ydata[k] < 0){
                y2[parseInt(k) + 1] = null;
            }else{
                y2[parseInt(k) + 1] = ydata[k];
            }
        }
        y.push(y2);
    }
    
    
    
    
    
    
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
        console.log(y[k]);
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
    var colors = ['#1565c0','#ffb300','#f44336','#7cb342','#26a69a'];
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
                    format: '%d/%m/%Y \n %H:%M',
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