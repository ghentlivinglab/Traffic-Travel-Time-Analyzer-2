var tab = 0;
var lastSelectedRoute = null;
var addedPeriods = 0;

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
addRouteToList = function(evt){
    lastSelectedRoute = evt.data.routeId;
};
    
function resetSlider(){
    while(tab !== 0){
        prevSlide();
    }
}
function showSlider(){
    //FADE IN
    $(".slider").fadeIn();
    //SLIDE IN LEFT
    //$(".slider").show();
    //$(".slider").css("left",-1000);
    //$(".slider").animate({left: '0px'});
}
function nextSlide(){
    tab++;
    tab %= 3;
    $(".slider").slider("next");
}
function prevSlide(){
    tab--;
    tab %= 3;
    $(".slider").slider("prev");
}
function showAvalibleRoutes(multiplicity){
    if(multiplicity === undefined) {
        multiplicity = "single";
    }
    $("#availableRoutesList").html("");
    switch(multiplicity){
      case "multi": 
          for(i=0; i<20; i++){
               $("#availableRoutesList")
                    .append($("<ol />")
                    .append($("<input />").attr("id","route"+i).attr("type","checkbox").attr("value",i).attr("name","routeId"))
                    .append($("<label />").attr("for","route"+i).mouseover(showRoutePreview).mouseleave(hideRoutePreview).click({routeId: i},addRouteToList)
                    .append($("<span />").text("R4: Gent - Zelzate")
                    )));
          }
           break;
      case "single": 
           for(i=0; i<20; i++){
               $("#availableRoutesList")
                    .append($("<ol />")
                    .append($("<input />").attr("id","route"+i).attr("type","radio").attr("value",i).attr("name","route").attr("name","routeId"))
                    .append($("<label />").attr("for","route"+i).mouseover(showRoutePreview).mouseleave(hideRoutePreview).click({routeId: i},addRouteToList)
                    .append($("<span />").text("R4: Gent - Zelzate")
                    )));
              }
          break;
  }
}

$(document).ready(function(){
  $(".slider").slider({
      full_width: true,
  });
  $(".slider").slider('pause');
  $(".slider").css("height","100%");
  $(".slides").css("height","100%");

  $("#btnCompareSources").click(function(){
      resetSlider();
      showSlider();
      showAvalibleRoutes();
  });
  $("#btnAvgTraffic").click(function(){
      resetSlider();
      showSlider();
      showAvalibleRoutes();
  });
  $("#btnDelayWeekday").click(function(){
      resetSlider();
      showSlider();
      showAvalibleRoutes();
  });
  $("#btnRushHours").click(function(){
      resetSlider();
      showSlider();
      showAvalibleRoutes();
  });
  $("#btnCompareRoutes").click(function(){
      resetSlider();
      showSlider();
      showAvalibleRoutes("multi");
  });
  $("#btnComparePeriods").click(function(){
      resetSlider();
      showSlider();
      showAvalibleRoutes();
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

                  

/*
$(document).ready(function() {
    
    $(".sidebar .content").css("height",$("main").height()-$(".sidebar .header").height());
    $(".datetimepicker").bootstrapMaterialDatePicker({ 
        format : 'DD MMMM YYYY - HH:mm',
        lang: 'nl',
        weekStart : 1        
    });
    
    $("#selectRoutes").click(function(){
        switch($(this).data("multiplicity")){
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
        $('#selectRoutesModel').openModal();
     });
});
*/