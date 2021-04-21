$(document).ready(function (){
    $("#apply-button").click(function () {
        var studyGroupApplicationForm = $('#apply-form').serialize();
        $.ajax({
            type: 'POST',
            url: '/apply_studygroup',
            data: studyGroupApplicationForm,
            dataType: 'json',
            success: function (result) {
                if (result.message == "ok"){
                    $(".modal-body").html('스터디신청을완료했습니다');
                    $("#exampleModalCenter").modal();
                    $('#exampleModalCenter').on('hidden.bs.modal', function () {
                        location.href='/'
                    })
                }
                else {
                    $(".modal-body").html(result.message);
                    $("#exampleModalCenter").modal();
                }
            }
        });
    });
});
