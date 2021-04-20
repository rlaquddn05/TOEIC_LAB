
    window.onload = function () {
        var error = [[${errorMessage}]];
        var success = [[${successMessage}]];
        if (error != null) {
            $('.modal-body').html(error);
            $('#errorModal').modal('show');
        }
        if (success != null) {
            $('.modal-body').html(success);
            $('#exampleModalCenter').modal('show');
            $('#exampleModalCenter').on('hidden.bs.modal', function () {
                location.href = '/'
            })
        }
    }
