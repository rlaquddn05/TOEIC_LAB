
    $(document).ready(function () {
        $('#chatContent').scrollTop($('#chatContent')[0].scrollHeight);


    });

    function add() {
        $.ajax({
            type: 'GET',
            url: '/add_comment',
            data: {"content": $('#comment').val(), "group": [[${thisStudyGroup.id}]]},
            dataType: 'json',
            success: function (result) {
                location.reload();
            }
        });
    }

function check() {
    $('.modal-body').html('정말로 해당 그룹을 탈퇴하시겠습니까?');
    $('#check_modal').modal('show');
}


function a(id) {
    $.ajax({
        type: 'GET',
        url: '/modify_study_leader',
        data: {"target": id, "group": [[${thisStudyGroup.id}]]},
        dataType: 'json',
        success: function (result) {
            $('.modal-body').html(result.message);
            $('#exampleModalCenter').modal('show');
            $('#exampleModalCenter').on('hidden.bs.modal', function () {
                location.reload();
            })
        }
    });
}

function b() {
    $.ajax({
        type: 'GET',
        url: '/modify_study_name',
        data: {"name": $('#name').val(), "group": [[${thisStudyGroup.id}]]},
        dataType: 'json',
        success: function (result) {
            $('.modal-body').html(result.message);
            $('#exampleModalCenter').modal('show');
            $('#exampleModalCenter').on('hidden.bs.modal', function () {
                location.reload();
            })
        }
    });
}

function c(test) {
    $.ajax({
        type: 'GET',
        url: '/secession_studyGroup',
        data: {id: test},
        dataType: 'json',
        success: function (result) {
            $('.modal-body').html(result.message);
            $('#exampleModalCenter').modal('show');
            $('#exampleModalCenter').on('hidden.bs.modal', function () {
                location.href = '/'
            })
        }
    });
}