$(document).ready(function () {
    $("#update_password_button").click(function () {
        $.ajax({
            type: 'GET',
            url: '/update/password',
            data: 'password=' + $("#password").val() + '&userId=' + $("#userId").val(),
            dataType: 'json',
            success: function (result){
                $(".modal-body").html(result.message);
                $("#myPageButton1").modal();
            }
        });
    });

    $("#delete_button").click(function () {
        $.ajax({
            type: 'GET',
            url: '/update/delete',
            data: 'userId=' + $("#userId").val(),
            dataType: 'json',
            success: function (result){
                $(".modal-body").html(result.message);
                $("#myPageButton2").modal();
            }
        });
    });

    $("#nickname_button").click(function (){
        $.ajax({
            type: 'GET',
            url: '/signup/checkNickname',
            data: 'nickname=' + $("#nickname").val(),
            dataType: 'json',
            success: function (result){
                $(".modal-body").html(result.message);
                $("#myPageButton1").modal();
                // if(result.message == "이미 존재하는 닉네임입니다."){
                //     $("#nickname_button").attr('disabled', false);
                // }else{
                //     $("#nickname_button").attr('disabled', true);
                // }
            }
        });
    });
});