
$(document).ready(function () {
    $("#user_id_button").click(function () {
        $.ajax({
            type: 'GET',
            url: '/signup/checkUserId',
            data: 'userId=' + $("#userId").val(),
            dataType: 'json',
            success: function (result){

                if(result.message == "이미 존재하는 아이디입니다."){
                    $(".modal-body").html(result.message);
                    $("#emailButton1").modal();
                    $("#user_id_button").attr('disabled', false);
                }else{
                    $(".modal-body").html("[" + $("#userId").val() + "]는 " + result.message);
                    $("#userThisId").modal();
                }

            }
        });
    });

    $("#check_password_button").click(function (){
        $.ajax({
            type: 'GET',
            url: '/signup/checkPasswords',
            data: 'password=' + $("#password").val() + '&check_password=' + $("#check_password").val(),
            dataType: 'json',
            success: function (result){
                $(".modal-body").html(result.message);
                $("#emailButton1").modal();
                    if(result.message == "비밀번호가 일치합니다."){
                    $("#check_password_button").attr('disabled', true);
                }else{
                    $("#check_password_button").attr('disabled', false);
                }
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
                $("#emailButton1").modal();
                    if(result.message == "이미 존재하는 닉네임입니다."){
                        $("#nickname_button").attr('disabled', false);
                    }else{
                        $("#nickname_button").attr('disabled', true);
                    }
            }
        });
    });

    $("#email_button").click(function (){
        $.ajax({
        type: 'GET',
        url: '/signup/email',
        data: 'email=' + $("#email").val(),
        dataType: 'json',
            success: function (result) {
                $(".modal-body").html(result.message);
                $("#emailButton1").modal();
            }
        });
    });

    $("#check_certification_number_button").click(function (){
        $.ajax({
        type: 'GET',
        url: '/signup/checkTokens',
        data: 'email=' + $("#email").val() + '&certification_number=' + $("#certification_number").val(),
        dataType: 'json',
        success: function (result){
            $(".modal-body").html(result.message);
            $("#emailButton1").modal();
                if(result.message == "이메일 인증 성공."){
                $("#check_certification_number_button").attr('disabled', true);
                }else{
                    $("#check_certification_number_button").attr('disabled', false);
                }
            }
        });
    });
});
function beforeSubmit(){
    if($("#user_id_button").prop('disabled')&&$("#check_password_button").prop('disabled')
        &&$("#nickname_button").prop('disabled')&&$("#check_certification_number_button").prop('disabled')){
        return true;
    }else{
        $(".modal-body").html("[중복확인]과 [확인]을 확인하세요.");
        $("#emailButton1").modal();
        return false;
    }
}