$(document).ready(function(){
    $("#add-RC-LC-meeting").click(function(){
        $("#RC-LC-box").append("<div>" +
            "<select class='form-line-select'>" +
            "<option selected>선택</option>"+
            "<option value='LC'>LC</option>"+
            "<option value='RC'>RC</option>"+
            "<select class='form-line-select'>"+
            "</div>")
    });
});