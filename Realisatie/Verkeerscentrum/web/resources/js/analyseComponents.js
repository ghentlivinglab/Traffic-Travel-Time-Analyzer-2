$(document).ready(function() {
    $(".sidebar .content").css("height",$("main").height()-$(".sidebar .header").height());
    /*$('.datepicker').pickadate({
        selectMonths: true, // Creates a dropdown to control month
        selectYears: 15 // Creates a dropdown of 15 years to control year
    });*/
     $('.datetimepicker').click(function() {
         /*var datetime = "#"+$(this).data("refid");
         alert(datetime);
         $(datetime).bootstrapMaterialDatePicker({ format : 'dddd DD MMMM YYYY - HH:mm' , lang : 'nl' });
         $(datetime).trigger("mouseup");
         */
    })
    $(".datetimepicker").bootstrapMaterialDatePicker({ format : 'DD MMMM YYYY - HH:mm' });
     //$('#modal1').openModal();
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