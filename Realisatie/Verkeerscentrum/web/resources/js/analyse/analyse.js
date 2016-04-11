
function switchToModus(modus){
    if(modus === null){
        modus = "graph";
    }
    var btn, tab;
    switch(modus){
        case "graph": btn = $("#btnSwitchToGraph"); tab = $("#chartTab"); break;
        case "table": btn = $("#btnSwitchToTable"); tab = $("#tableTab"); break;
        case "map": btn = $("#btnSwitchToMap"); tab = $("#mapTab"); break;
    }
    
    $(".modusBtnGroup li").removeClass("active");
    btn.parent().addClass("active");
    $(".analyseContent > div").hide();
    tab.show();
}

$("#btnSwitchToGraph").click(function(){
    switchToModus("graph");
});


$("#btnSwitchToTable").click(function(){
    switchToModus("table");
});

switchToModus("graph");

function refreshAnalysePre(){
    $("[name=periodStartDummy]").attr("disabled", "disabled");
    $("[name=periodEndDummy]").attr("disabled", "disabled");
    return true;
}