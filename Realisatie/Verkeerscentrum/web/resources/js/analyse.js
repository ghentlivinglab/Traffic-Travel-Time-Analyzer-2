var tab = 0;
var lastSelectedRoute = null;
var addedPeriods = 0;
var inittabs = [];

showRoutePreview = function(){
    $("#routePreview").css("display","block");
    $("#routePreview").attr("src","http://localhost:8080/web/resources/img/traject.PNG");
};
hideRoutePreview = function(){
    if(lastSelectedRoute === null)
        $("#routePreview").css("display","none");
    else
        $("#routePreview").attr("src","http://localhost:8080/web/resources/img/traject.PNG");
};
addRouteToList = function(){
    lastSelectedRoute = $("#"+$(this).attr("for")).val();
};
    
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
function setRouteMultiplicity(multiplicity){
    if(multiplicity === undefined) {
        multiplicity = "single";
    }
    switch(multiplicity){
      case "multi": 
           $("[name=routeId]").attr("type","checkbox");
           break;
      case "single": 
           $("[name=routeId]").attr("type","radio");
           break;
    }
}
function setActiveNavTab(btn){
    $(".navigation li").removeClass("active");
    btn.parent("li").addClass("active");
}

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


$(document).ready(function(){

  $("#availableRoutesList label").mouseover(showRoutePreview)
          .mouseleave(hideRoutePreview)
          .click(addRouteToList);
    
  $("#btnCompareSources").click(function(){
      inittabs = ["singleperiod","singleroute","providers"];
      resetSlider();
      showSlider();
      setRouteMultiplicity();
      setActiveNavTab($(this));
  });
  $("#btnAvgTraffic").click(function(){
      inittabs = ["singleperiod","singleroute","providers"];
      resetSlider();
      showSlider();
      setRouteMultiplicity();
      setActiveNavTab($(this));
  });
  $("#btnDelayWeekday").click(function(){
      inittabs = ["singleperiod","singleroute","providers"];
      resetSlider();
      showSlider();
      setRouteMultiplicity();
      setActiveNavTab($(this));
  });
  $("#btnRushHours").click(function(){
      inittabs = ["singleperiod","singleroute","providers"];
      resetSlider();
      showSlider();
      setRouteMultiplicity();
      setActiveNavTab($(this));
  });
  $("#btnCompareRoutes").click(function(){
      inittabs = ["singleperiod","singleroute","providers"];
      resetSlider();
      showSlider();
      setRouteMultiplicity("multi");
      setActiveNavTab($(this));
  });
  $("#btnComparePeriods").click(function(){
      inittabs = ["multiperiod","singleroute","providers"];
      resetSlider();
      showSlider();
      setRouteMultiplicity();
      setActiveNavTab($(this));
  });

  $(".btnNextSlide").click(nextSlide);
  $(".btnPrevSlide").click(prevSlide);

  $(".datetimepicker").bootstrapMaterialDatePicker({ 
      format : 'DD MMMM YYYY - HH:mm',
      lang: 'nl',
      weekStart : 1
  }).on('change', function(e, date){
      if($(this).attr('id')){
          var hiddenName = $(this).attr('id'); 
          $("[name="+hiddenName+"]").val(new Date(date).getTime());
      }
  });
  
  $(".datepicker").bootstrapMaterialDatePicker({ 
      format : 'DD MMMM YYYY',
      time: false,
      lang: 'nl',
      weekStart : 1
  }).on('change', function(e, date){
      if($(this).attr('id')){
          var hiddenName = $(this).attr('id'); 
          var date = new Date(date);
          date.setMinutes(0);
          date.setHours(0);
          date.setSeconds(0);
          date.setMilliseconds(0);
          $("[name="+hiddenName+"]").val(date.getTime());
      }
  });
  
  $('.btnRefreshAnalyse').click(function(e) {
        e.preventDefault();
        $('#formRefreshAnalyse').submit(function() {
            $(this).children('[name=periodStartDummy]').remove();
            $(this).children('[name=periodEndDummy]').remove();
         });
  });
  
  $("#btnSwitchToGraph").click(function(){
        switchToModus("graph");
  });


  $("#btnSwitchToTable").click(function(){
        switchToModus("table");
  });
  
  switchToModus("graph");

});

$("#btnAddNewPeriod").click(function(){
    
    btnEdit = $("<a />").attr("href","#!").addClass("btn-floating blue").append($("<i />").addClass("material-icons").text("create"));
    btnRemove = $("<a />").attr("href","#!").addClass("btn-floating red").append($("<i />").addClass("material-icons").text("delete"));
    if(addedPeriods === 0 || addedPeriods === null) $("#newPeriodList tbody").html("");
    $("#newPeriodList tbody").append(
            $("<tr />").append(
            $("<td />").text($("#txtNewPeriodStart").val())
            ).append(
            $("<td />").text($("#txtNewPeriodEnd").val())
            ).append(
            $("<td />").addClass("right-align").append(btnEdit).append(btnRemove)
          ));
    addedPeriods++;
    $("#txtNewPeriodStart").val("");
    $("#txtNewPeriodEnd").val("");
    oldPeriodStart = $("[name=periodsStart]").val();
    oldPeriodEnd = $("[name=periodsEnd]").val();
    newPeriodStart = $("[name=txtNewPeriodStart]").val();
    newPeriodEnd = $("[name=txtNewPeriodEnd]").val();
    if(oldPeriodStart != "") {
        oldPeriodStart = oldPeriodStart+" ";
        oldPeriodEnd = oldPeriodEnd+" ";
    }
    $("[name=periodsStart]").val(oldPeriodStart+""+newPeriodStart);
    $("[name=periodsEnd]").val(oldPeriodEnd+""+newPeriodEnd);
   
});

$("#btnSelectRoutes").click(function(){
    /*switch($(this).data("multiplicity")){
        case "multi": 
            for(i=0; i<20; i++){
                 $("#availableRoutesList")
                         .append($("<li />")
                         .append($("<input />").attr("id","route"+i).attr("type","checkbox"))
                         .append($("<label />").attr("for","route"+i).mouseover(showRoutePreview).mouseleave(hideRoutePreview).click(addRouteToList)
                         .append($("<span />").text("R4: Gent - Zelzate")
                         )));
            }
             break;
        case "single": 
             for(i=0; i<20; i++){
                 $("#availableRoutesList")
                         .append($("<li />")
                         .append($("<input />").attr("id","route"+i).attr("type","radio").attr("name","route"))
                         .append($("<label />").attr("for","route"+i).mouseover(showRoutePreview).mouseleave(hideRoutePreview).click(addRouteToList)
                         .append($("<span />").text("R4: Gent - Zelzate")
                         )));
                }
            break;
    }
    */
    $('#selectRoutesModel').openModal();
 });

                  

/*
$(document).ready(function() {
    
    $(".sidebar .content").css("height",$("main").height()-$(".sidebar .header").height());
    $(".datetimepicker").bootstrapMaterialDatePicker({ 
        format : 'DD MMMM YYYY - HH:mm',
        lang: 'nl',
        weekStart : 1        
    });
    
    
});
*/