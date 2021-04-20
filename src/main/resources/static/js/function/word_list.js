
function word(test) {
    alert(test)
        $.ajax({
        type: 'GET',
        url: '/delete_word',
        data: {"word": test},
        dataType: 'json',
        success: function (result) {
            $('.modal-body').html(result.message);
            $('#exampleModalCenter').modal('show');
    },
    });
}
