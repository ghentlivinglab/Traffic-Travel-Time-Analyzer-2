$(document).ready(function() {
    /*
    $('#selectRoutesModel').openModal({
      dismissible: false, // Modal can be dismissed by clicking outside of the modal
      opacity: .5, // Opacity of modal background
      in_duration: 300, // Transition in duration
      out_duration: 200, // Transition out duration
      //ready: function() { alert('Ready'); }, // Callback for Modal open
      //complete: function() { alert('Closed'); } // Callback for Modal close
    });
    */
    $(".sidebar .content").css("height",$("main").height()-$(".sidebar .header").height());
    $(".datetimepicker").bootstrapMaterialDatePicker({ 
        format : 'DD MMMM YYYY - HH:mm',
        lang: 'nl',
        weekStart : 1        
    });
    showRoutePreview = function(){
         $("#routePreview").css("display","block");
         
         $("#routePreview").attr("src","http://localhost:8080/web/resources/img/traject.PNG");
    };
    hideRoutePreview = function(){
         $("#routePreview").css("display","none");
    };
    addRouteToList = function(){
         
    };
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