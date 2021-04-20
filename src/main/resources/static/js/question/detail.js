
$(document).ready(function (){
    $("#addReview").click(function (){
        $.ajax({
            type: 'GET',
            url: '/add_review_note',
            data: {"id":[[${question.id}]],"answer":[[${userAnswer.get(question.id)}]]},
            dataType: 'json',
            success: function (result){
                $('.modal-body').html(result.message);
                $('#exampleModalCenter').modal('show');
            }
        });
    });
});
