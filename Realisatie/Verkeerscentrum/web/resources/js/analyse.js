var tab = 0;
var lastSelectedRoute = null;
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
     var hiddenName = $(this).attr('id'); 
     $("[name="+hiddenName+"]").val(new Date(date).getTime());
  });
  

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