$(document).ready(function () {
    $("#likeNumber").click(function () {
        $.ajax({
            type: 'GET',
            url: '/bulletinDetail/likeNumber',
            data: 'id=' + $("#id").val(),
            dataType: 'json',
            success: function (result){
                $(".modal-body").html(result.message);
                $("#emailButton1").modal();
            }
        });
    });

    $("#deleteBulletin").click(function () {
            $.ajax({
                type: 'GET',
                url: '/bulletinDetail/deleteBulletin',
                data: 'id=' + $("#id").val() + '&writerId=' + $("#writerId").val(),
                dataType: 'json',
                success: function (result){
                    $(".modal-body").html(result.message);
                    $("#emailButton2").modal();
            }
        });
    });

});
