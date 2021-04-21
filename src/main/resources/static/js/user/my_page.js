$(document).ready(function (){
    $("#nickname-button").click(function (){
        $.ajax({
            method: 'GET',
            url: '/update/checkNickname',
            data: 'nickname=' + $('#nickname').val(),
            dataType: 'json',
            success: function (result) {
                $('.modal-body').html(result.message);
                $('#show-modal').modal('show');
            }
        });
    });

    $("#update_password_button").click(function () {
        $.ajax({
            type: 'GET',
            url: '/update/password',
            data: 'password=' + $("#password").val() + '&userId=' + $("#userId").val(),
            dataType: 'json',
            success: function (result) {
                $(".modal-body").html(result.message);
                $("#show-modal").modal();
            }
        });
    });

    $("#delete_button").click(function () {
        $.ajax({
            type: 'GET',
            url: '/update/delete',
            data: 'userId=' + $("#userId").val(),
            dataType: 'json',
            success: function (result) {
                $(".modal-body").html(result.message);
                $("#myPageButton2").modal();
            }
        });
    });

    $("#modify-button").click(function () {
        var updateForm = $('#form').serialize();
        $.ajax({
            type: 'POST',
            url: '/my_page',
            data: updateForm,
            dataType: 'json',
            success: function (result) {
                $(".modal-body").html(result.message);
                $("#show-modal").modal();
            }
        });
    });

});

