

$(document).ready(function () {
    $("#reset_password_email_token_check_button").click(function (){
        $.ajax({
            type: 'GET',
            url: '/reset/checkTokens',
            data: 'resetPasswordEmail=' + $("#resetPasswordEmail").val() + '&token=' + $("#token").val(),
            dataType: 'json',
            success: function (result){
                $(".modal-body").html(result.message);
                $("#emailButton2").modal();
            }
        });
    });
});
