
$(document).ready(function (){
    $("#find").click(function (){
        $.ajax({
            type: 'GET',
            url: '/popup_dictionary_find/'+ $("#inputWord").val().toString(),
            dataType: 'json',
            success: function(data){
                var word = JSON.stringify(data.word);
                var meaning = JSON.stringify(data.meaning);
                $('input[name=word]').attr('value',word);
                $('input[name=mean]').attr('value',meaning);
            },
            error: function(){
                alert("검색한 단어를 찾지 못하였습니다."+"반대로 입력해보세요.")
            }
        });
    });


    $("#add_word_list").click(function (){
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
            $.ajax({
            type: 'POST',
            url: '/add_word_list',
            data: {"word": $("#word").val().toString(), "meaning": $("#meaning").val().toString(), "_csrf":token},
            dataType: 'json',
            success: function(result){
                $('.modal-body').html(result.message);
                $('#exampleModalCenter').modal('show');
            },
        });
    });
});
