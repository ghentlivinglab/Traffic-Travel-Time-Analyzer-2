/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//misschien bruikbaar
/*
 * 
 * @param {type} param
 * 
 * 
 * http://momentjs.com/docs/#/displaying/format/
 * 
 * Misschien in REST de volgorde van de data mooi teruggeven zoals
 * nr: 1
 * timestamp: ..
 * [Google{
 * Duration:
 * Distance:
 * }
 * Here {
 * ...
 * 
 * 
 * nr : 2 ...
 */


var urlProviderComparer = "http://localhost:8080/RestApi/v2/routes/all/providerDifference?start=1460052017194&end=1460123828236";


$(document).ready(function () {
    $.ajax({
        url: urlProviderComparer,
        dataType: "json",
        success: viewAnalyseData,
        error: function(jqXHR, textStatus, errorThrown ){
            Materialize.toast('Er kan geen nieuwe data worden opgehaald!', 4000, 'toast bottom error');
        }
    });
});



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

function viewAnalyseData(data){
    
    console.log(data);
    
    var route = data[0];
    
    //
    // PARSE
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
            y2[parseInt(k) + 1] = ydata[k];
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
    row = $("<tr />");
    col = $("<th />").text("");
    row.append(col);
    for(i=1; i<x.length; i++){
        col = $("<td />").text(x[i].format("dd/mm")+"\n"+x[i].format("HH:MM")); 
        row.append(col);
    }
    table.append(row);
    
    
    //DATA
    for(k=0; k<y.length; k++){
        row = $("<tr />");
        var providerName = y[k][0];
        col = $("<th />").text(providerName);
        row.append(col);        
        console.log(y[k]);
        for(i=1; i<y[k].length; i++){
            ydata = y[k][i];
            col = $("<td />").text(Math.floor(ydata/60/1000)+" min");
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
        colors[y[i][0]] = colors[i];
    }

    
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
                    format: '%a %I:%M',
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
                type: 'timeseries',
                tick: {
                    format: d3.time.format('%M\'%S\"'),
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
                value: function (v) {
                    var mind = v / 60000;
                    var minutes = Math.floor(mind);

                    var secd = (mind % minutes) * 60;
                    var seconds = Math.floor(secd);

                    var string = minutes + 'm ' + seconds;
                    return string;
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











function myFunction(data) {

    /*var d = new Date(1382086394000);
     var a = d.getHours() + '.' + d.getMinutes();
     var tijd = 651;
     var min = 60;
     var div = Math.floor(tijd / min);
     var rem = (tijd % min) / 100;
     var b = div + rem;
     */
    var obj = data[0]; //data uit ajax-call
        
        console.log(obj);
        
        
    var xdata = obj[0]["data"]["duration"]["x-ax"];
    var x = [];
    x[0] = 'x';
    var xresult = xdata;
    for (var k in xresult) {
        x[parseInt(k) + 1] = xresult[k];
    }
    
    var ydata = obj[0]["data"]["duration"]["y-ax"]; //array y-as
    var y = [];
    y[0] = 'y';
    var xresult = ydata;

    for (var k in xresult) {
        if (xresult[k] === 'null')
            xresult[k] = null;
        if (xresult[k] !== null)
            xresult[k] = new Date(xresult[k] * 1000).getTime();
        y[parseInt(k) + 1] = xresult[k];
        console.log(k, xresult[k]);
    }

    chart = c3.generate({
        bindto: '#chart',
        data: {
            x: 'x',
            columns: [
                x,
                y,
                
                 ['Google', 650, 651, 655, 750, 755, 720, 635, 637, 645, 675, 700, 730, 720, 640, 645, 650, 720, 735, 710],
                 ['Here', 640, 645, 650, 720, 735, 710, 650, 651, 655, 750, 755, 720, 635, 637, 645, 675, 700, 730, 720],
                 ['TomTom', 635, 637, 645, 675, 700, 730, 720, 640, 645, 650, 720, 735, 710, 650, 651, 655, 750, 755, 720],
                 ['Coyote', 630, 632, 640, 670, 690, 720, 740, 650, 666, 675, 735, 712, 735, 735, 657, 682, 740, 760, 730],
                 ['Waze', 640, 641, 645, 740, 745, 710, 625, null, null, 655, 690, 720, 710, 630, 635, 640, 710, 725, 740],
                 //  ['Gemiddelde', 635, 635, 635, 635, 635, 635, 635, 635, 635, 635, 635, 635, 635, 635, 635, 635, 635, 635, 635],
                 ['Ideaal', 625, 625, 625, 625, 625, 625, 625, 625, 625, 625, 625, 625, 625, 625, 625, 625, 625, 625, 625]

            ],
            colors: {
                Here: '#1565c0', //#1565c0 blue darken-3
                'Google': '#ffb300', //#ffb300 amber darken-1
                TomTom: '#f44336', // #f44336 red
                Coyote: '#7cb342', // #7cb342 light-green darken-1
                Waze: '#26a69a', //#26a69a teal lighten-1  
                Gemiddelde: '#9c27b0', //#9c27b0 purple
                Ideaal: '#000000'//#000000 black


            }
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
                    format: '%a %I:%M',
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
                type: 'timeseries',
                tick: {
                    format: d3.time.format('%M\'%S\"'),
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
                value: function (v) {
                    var mind = v / 60000;
                    var minutes = Math.floor(mind);

                    var secd = (mind % minutes) * 60;
                    var seconds = Math.floor(secd);

                    var string = minutes + 'm ' + seconds;
                    return string;
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
;
