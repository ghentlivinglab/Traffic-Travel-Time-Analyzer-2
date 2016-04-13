/* global Materialize */

var tab = 0;
var inittabs = [];

$("#btnCompareSources").click(function(event){
    inittabs = ["singleperiod","route","providers"];
    resetSlider();
    showSlider();
    setRouteMultiplicity();
    setPeriodMultiplicity();
    setActiveNavTab($(this));
    setFormURL($(this).attr("href"));
    event.preventDefault();
});

$("#btnAvgTraffic").click(function(event){
    inittabs = ["singleperiod","route","providers"];
    resetSlider();
    showSlider();
    setRouteMultiplicity();
    setPeriodMultiplicity();
    setActiveNavTab($(this));
    setFormURL($(this).attr("href"));
    event.preventDefault();
});

$("#btnDelayWeekday").click(function(event){
    inittabs = ["singleperiod","route","providers"];
    resetSlider();
    showSlider();
    setRouteMultiplicity();
    setPeriodMultiplicity();
    setActiveNavTab($(this));
    setFormURL($(this).attr("href"));
    event.preventDefault();
});

$("#btnRushHours").click(function(event){
    inittabs = ["singleperiod","route","providers"];
    resetSlider();
    showSlider();
    setRouteMultiplicity();
    setPeriodMultiplicity();
    setActiveNavTab($(this));
    setFormURL($(this).attr("href"));
    event.preventDefault();
});

$("#btnCompareRoutes").click(function(event){
    inittabs = ["singleperiod","route","providers"];
    resetSlider();
    showSlider();
    setRouteMultiplicity("multi");
    setPeriodMultiplicity();
    setActiveNavTab($(this));
    setFormURL($(this).attr("href"));
    event.preventDefault();
});

$("#btnComparePeriods").click(function(event){
    inittabs = ["multiperiod","route","providers"];
    resetSlider();
    showSlider();
    setRouteMultiplicity();
    setPeriodMultiplicity("multi");
    setActiveNavTab($(this));
    setFormURL($(this).attr("href"));
    event.preventDefault();
});

$(".btnNextSlide").click(nextSlide);
$(".btnPrevSlide").click(prevSlide);
  
    
function showTab(tabname){
    $(".inittabs li").hide();
    $("#inittab_"+tabname).addClass("active").show();
}   
    
function resetSlider(){
    if(inittabs.length > 0){
        tab = 0;
        showTab(inittabs[0]);
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
    showTab(inittabs[tab]);
}
function prevSlide(){
    tab--;
    tab %= inittabs.length;
    showTab(inittabs[tab]);
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

