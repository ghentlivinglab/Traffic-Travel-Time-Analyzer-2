/* global Materialize */

var tab = 0;
var inittabs = [];
var descriptions = [];

$("#btnCompareSources").click(function(event){
    inittabs = ["singleperiod","route","providers"];
    descriptions = [
        "Selecteer over welke periode de data moet worden uitgemiddeld",
        "",
        ""
    ];
    resetSlider();
    showSlider();
    setRouteMultiplicity();
    deSelectAllRoutes();
    setPeriodMultiplicity();
    setActiveNavTab($(this));
    setFormURL($(this).attr("href"));
    event.preventDefault();
});

$("#btnAvgTraffic").click(function(event){
    inittabs = ["singleperiod","route","providers"];
    descriptions = [
        "Selecteer over welke periode de data moet worden uitgemiddeld",
        "",
        ""
    ];
    resetSlider();
    showSlider();
    setRouteMultiplicity("multi");
    selectAllRoutes();
    setPeriodMultiplicity();
    setActiveNavTab($(this));
    setFormURL($(this).attr("href"));
    event.preventDefault();
});

$("#btnDelayWeekday").click(function(event){
    inittabs = ["singleperiod","route","providers"];
    descriptions = [
        "Selecteer over welke periode de data moet worden uitgemiddeld",
        "",
        ""
    ];
    resetSlider();
    showSlider();
    deSelectAllRoutes();
    setRouteMultiplicity();
    setPeriodMultiplicity();
    setActiveNavTab($(this));
    setFormURL($(this).attr("href"));
    event.preventDefault();
});

$("#btnRushHours").click(function(event){
    inittabs = ["singleperiod","route","providers"];
    descriptions = [
        "Selecteer over welke periode de data moet worden uitgemiddeld",
        "",
        ""
    ];
    resetSlider();
    showSlider();
    deSelectAllRoutes();
    setRouteMultiplicity();
    setPeriodMultiplicity();
    setActiveNavTab($(this));
    setFormURL($(this).attr("href"));
    event.preventDefault();
});

$("#btnCompareRoutes").click(function(event){
    inittabs = ["singleperiod","route","providers"];
    descriptions = [
        "Selecteer over welke periode de data moet worden uitgemiddeld",
        "",
        ""
    ];
    resetSlider();
    showSlider();
    deSelectAllRoutes();
    setRouteMultiplicity("multi");
    setPeriodMultiplicity();
    setActiveNavTab($(this));
    setFormURL($(this).attr("href"));
    event.preventDefault();
});

$("#btnComparePeriods").click(function(event){
    inittabs = ["multiperiod","route","providers"];
    descriptions = [
        "Selecteer over welke periode de data moet worden uitgemiddeld",
        "",
        ""
    ];
    resetSlider();
    showSlider();
    deSelectAllRoutes();
    setRouteMultiplicity();
    setPeriodMultiplicity("multi");
    setActiveNavTab($(this));
    setFormURL($(this).attr("href"));
    event.preventDefault();
});

$(".btnNextSlide").click(nextSlide);
$(".btnPrevSlide").click(prevSlide);
  
    
function showTab(tabid){
    $(".inittabs li").hide();
    $("#inittab_"+inittabs[tabid]).addClass("active").show();
    $("#inittab_"+inittabs[tabid]+" .description").text(descriptions[tabid]);
}   
    
function resetSlider(){
    if(inittabs.length > 0){
        tab = 0;
        showTab(0);
    }else{
        alert("No tabs are defined!");
    }
}

function showSlider(){
    //FADE IN
    $(".initprocess").fadeIn();
    $(".inittabs").fadeIn();
    //SLIDE IN LEFT
    //$(".slider").show();
    //$(".slider").css("left",-1000);
    //$(".slider").animate({left: '0px'});
}

function nextSlide(){
    tab++;
    tab %= inittabs.length;
    showTab(tab);
}
function prevSlide(){
    tab--;
    tab %= inittabs.length;
    showTab(tab);
}

function setActiveNavTab(btn){
    $(".navigation li").removeClass("active");
    btn.parent("li").addClass("active");
}

function setFormURL(url){
    $("#analyseInitForm").attr("action",url);
}

$(document).ready(function(){
    Materialize.showStaggeredList('#staggered-list');
});   


var form = $("#analyseInitForm");
var formAddNewPeriod = $("#formAddNewPeriod");


$(".btnValidateSinglePeriodForm").click(function(event){
    form.validate({
        errorClass: "invalid",
        rules: {
            periodStartDummy: "required",
            periodEndDummy: "required"
        },
        messages: {
            periodStartDummy: "Please enter your firstname",
            periodEndDummy: "Please enter your lastname"
        },
        onclick: function(el, evt){
            alert("jaja");
        }
    });
    if(form.valid()){
        nextSlide();
    }
});



$(".btnValidateMultiPeriodForm").click(function(event){
    var rows = $("#newPeriodList tbody tr");
    if(rows.size() > 1){
        nextSlide();
    }else{
        alert("Gelieve 2 of meer perodes op te geven");
    }
});

$(".btnValidateRouteForm").click(function(event){
    var elements = $("[name=routeId");
    var n = 0;
    $('[name=routeId]').each(function() { //loop through each checkbox
        if(this.checked == true){
            n++;
        }               
    });
    var type = elements.first().attr("type");
    if(type === "checkbox"){
        if(n>1){
            nextSlide();
        }else{
            alert("Gelieve 2 of meer trajecten te selecteren");
        }
    }
    if(type === "radio"){
        if(n===1){
            nextSlide();
        }else{
            alert("Gelieve een traject te selecteren");
        }
    }
});


$(".btnValidateProviderForm").click(function(event){
    var elements = $("[name=provider");
    var n = 0;
    $('[name=provider]').each(function() { //loop through each checkbox
        if(this.checked == true){
            n++;
        }               
    });
    if(n<1){
        alert("Gelieve 1 of meerdere providers te selecteren");
        event.stopPropagation();
        event.preventDefault();
    }
});

