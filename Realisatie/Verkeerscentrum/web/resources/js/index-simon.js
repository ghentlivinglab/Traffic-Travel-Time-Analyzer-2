$("#btnRoutes").click(function(event){
    inittabs = ["routes"];
    resetSlider();
    showSlider();
    setActiveNavTab($(this));
    setFormURL($(this).attr("href"));
    event.preventDefault();
});

$("#btnDataSources").click(function(event){
    inittabs = ["datasources"];
    resetSlider();
    showSlider();
    setActiveNavTab($(this));
    setFormURL($(this).attr("href"));
    event.preventDefault();
});

$("#btnInterval").click(function(event){
    inittabs = ["interval"];
    resetSlider();
    showSlider();
    setActiveNavTab($(this));
    setFormURL($(this).attr("href"));
    event.preventDefault();
});