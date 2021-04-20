
function a(test){
    $.ajax({
        type: 'GET',
        url: '/delete_review_note',
        data: {id:test},
        dataType: 'json',
        success: function (result){
            $('.modal-body').html(result.message);
            $('#exampleModalCenter').modal('show');
            $('#exampleModalCenter').on('hidden.bs.modal', function () {
                location.reload();
            })
        }
    });
}
