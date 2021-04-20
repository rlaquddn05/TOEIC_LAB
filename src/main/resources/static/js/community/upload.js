
$(function (){
    $('.custom-file-input').on('change', function () {
        var files = $(this)[0].files;
        name = '';
        for (var i = 0; i < files.length; i++) {
            name += '\"' + files[i].name + '\"' + (i !== files.length - 1 ? ", " : "");
        }
        $(".custom-file-label").html(name);
    });

    $('#readText').on('click', function (){
            uploadFile();
        });
    });

    function uploadFile(){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    var form = $('#file_form')[0];
    var formData = new FormData(form);
    console.log(formData);
    for (var pair of formData.entries()) {
    console.log(pair[0]+ ', ' + pair[1]);
    }
    $.ajax({
    url: '/upload_img',
    data: formData,
    contentType: false,
    processData: false,
    type: 'POST',
    beforeSend: function(xhr) {
    xhr.setRequestHeader(header, token);
    },
    success: function (data){
            document.getElementById("image-to-text").innerHTML = data;
        }
    });
}

