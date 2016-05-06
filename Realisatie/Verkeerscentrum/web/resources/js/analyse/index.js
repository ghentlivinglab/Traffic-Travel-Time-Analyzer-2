/* global Materialize */

var tab = 0;
var inittabs = [];
var descriptions = [];
var periodValidationRequired = true;

$("#btnCompareSources").click(function(event){
    inittabs = ["singleperiod","route","providers"];
    descriptions = [
        "OPTIONEEL: Selecteer over welke periode de data moet worden weergegeven.",
        "Selecteer een traject.",
        "Selecteer welke bronnen je wil vergelijken met elkaar."
    ];
    periodValidationRequired = false;
    setPeriodType("datetime");
    setTitle("Databronnen vergelijken");
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
        "OPTIONEEL: Selecteer over welke periode de data moet worden uitgemiddeld. ",
        "Selecteer een traject",
        "Selecteer de bronnen die mogen worden opgenomen in de vergelijking"
    ];
    periodValidationRequired = false;
    setPeriodType("datetime");
    setTitle("Gemiddelde verkeerssituatie");
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
        "OPTIONEEL: Selecteer over welke periode de data moet worden weergeven",
        "Selecteer een traject",
        "Selecteer de bronnen die mogen worden opgenomen in de vergelijking"
    ];
    periodValidationRequired = false;
    setPeriodType("date");
    setTitle("Weekdagen vergelijken");
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
        "OPTIONEEL: Selecteer over welke periode de data moet worden uitgemiddeld",
        "Selecteer een traject",
        "Selecteer de bronnen die mogen worden opgenomen in de vergelijking"
    ];
    periodValidationRequired = false;
    setPeriodType("date");
    setTitle("Spitsuren");
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
        "OPTIONEEL: Selecteer over welke periode de data moet worden uitgemiddeld",
        "Selecteer de trajecten die je met elkaar wil vergelijken",
        "Selecteer de bronnen die mogen worden opgenomen in de vergelijking"
    ];
    periodValidationRequired = false;
    setPeriodType("datetime");
    setTitle("Trajecten vergelijken");
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
        "Selecteer welke perioden je wil vergelijken met elkaar",
        "Selecteer een traject",
        "Selecteer de bronnen die mogen worden opgenomen in de vergelijking"
    ];
    periodValidationRequired = true;
    setPeriodType("date");
    setTitle("Perioden vergelijken");
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
  
    
function setPeriodType(type){
    console.log(type);
    $("[name=periodStartDummy]").remove();
    $("[name=periodEndDummy]").remove();
    if(type == "datetime"){
        var datepickerinputStart = $("<input />").attr("type","text").addClass("datetimepicker").attr("id","periodStart").attr("name","periodStartDummy");
        $("[name=periodStart]").parent().append(datepickerinputStart);
        var datepickerinputStart = $("<input />").attr("type","text").addClass("datetimepicker").attr("id","periodEnd").attr("name","periodEndDummy");
        $("[name=periodEnd]").parent().append(datepickerinputStart);
    }
    if(type == "date"){
        var datepickerinputStart = $("<input />").attr("type","text").addClass("datepicker").attr("id","periodStart").attr("name","periodStartDummy");
        $("[name=periodStart]").parent().append(datepickerinputStart);
        var datepickerinputStart = $("<input />").attr("type","text").addClass("datepicker").attr("id","periodEnd").attr("name","periodEndDummy");
        $("[name=periodEnd]").parent().append(datepickerinputStart);    
    }
    setPeriodPicker();
}

function setTitle(title){
    $(".inittabs .title").text(title);
} 


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
    if(periodValidationRequired){
        form.validate({
            errorClass: "invalid",
            rules: {
                periodStartDummy: "required",
                periodEndDummy: "required"
            },
            messages: {
                periodStartDummy: "Selecteer het begin van de periode",
                periodEndDummy: "Selecteer het einde van de periode"
            }
        });
        if(form.valid()){
            nextSlide();
        }
    }else{
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

