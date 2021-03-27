$(document).ready(function(){
    $("#add-RC-LC-meeting").click(function(){
        add_LC_RC_Row();
        $('.btn-LC-RC-Remove').click(function(){
            /* $(".select-form-div").remove();*/
            $(this).prev().prev().prev().remove()
            $(this).prev().prev().remove()
            $(this).prev().remove()
            $(this).next().remove()
            $(this).remove();
            /*$("#select-form-div").remove();*/

        });
    });
});

function add_LC_RC_Row(){

    var html ='<select class="form-line-select" th:name="select-form1" name="">\
            <option selected>선택</option>\
            <option th:value="LC">LC</option>\
            <option th:value="RC">RC</option></select>\
            <select class="form-line-select" th:name="select-form2">\
            <option selected>PART 선택</option>\
            <option th:value="PART1">PART1</option>\
            <option th:value="PART2">PART2</option>\
            <option th:value="PART3">PART3</option>\
            <option th:value="PART4">PART4</option></select>\
            <select class="form-line-select" th:name="select-form3">\
            <option selected>문항선택</option>\
            <option th:value="5">5</option>\
            <option th:value="10">10</option>\
            <option th:value="15">15</option>\
            <option th:value="20">20</option></select>\
            <button type="button" class="btn far fa-trash-alt btn-LC-RC-Remove"></button><br>';

    $("#RC-LC-box").append(html);
}

/*================================SPK==========================================*/
$(document).ready(function(){
    $("#add-SPK-meeting").click(function(){
        add_SPK_Row();
        $('.btn-SPK-Remove').click(function(){
            $(this).prev().prev().remove()
            $(this).prev().remove()
            $(this).next().remove()
            $(this).remove();

        });
    });
});


function add_SPK_Row(){

    var html ='<select class="form-line-select" th:name="select-form2">\
            <option selected>PART 선택</option>\
            <option value="PART1">PART1</option>\
            <option value="PART2">PART2</option>\
            <option value="PART3">PART3</option>\
            <option value="PART4">PART4</option></select>\
            <select class="form-line-select" th:name="select-form3">\
            <option selected>문항선택</option>\
            <option value="5">5</option>\
            <option value="10">10</option>\
            <option value="15">15</option>\
            <option value="20">20</option></select>\
            <button type="button" class="btn far fa-trash-alt btn-SPK-Remove"></button><br>';

    $("#SPK-box").append(html);
}



$( document ).ready(function(){

    //테스트용 데이터
    var sel1 = {
        " ":"유형선택",
        "LC":"LC",
        "RC":"RC"
    };

    //sel1이 LC 일경우
    var sel2_1 = {
        " ":"PART 선택",
        "PART1": "PART1",
        "PART2": "PART2",
        "PART3": "PART3",
        "PART4": "PART4"
    };

    //sel1이 RC 일경우
    var sel2_2 = {
        " ":"남성검진항목 선택",
        "PART5": "PART5",
        "PART6": "PART6",
        "PART7": "PART7"
    };

    //sel1에 서버에서 받아온 값을 넣기위해..
    // map배열과 select 태그 id를 넘겨주면 option 태그를 붙여줌.
    // map[키이름] = 그 키에 해당하는 value를 반환한다.
    //retOption(데이터맵, select함수 id)
    function retOption(mapArr, select){
        var html = '';
        var keys = Object.keys(mapArr);
        for (var i in keys) {
            html += "<option value=" + "'" + keys[i] + "'>" + mapArr[keys[i]] + "</option>";
        }

        $("select[id='" + select +"']").html(html);
    }

    $("select[id='sel1']").on("change", function(){
        var option = $("#sel1 option:selected").val();
        var subSelName = '';
        if(option == "LC") {
            subSelName = "sel2_1";
        } else if(option == "RC"){
            subSelName = "sel2_2";
        } else{
            $("#sel2").hide();
            return;
        }
        $('#sel2').show();
        retOption(eval(subSelName), "sel2");
    })
    retOption(sel1, "sel1");
});

for(var i=1; i<=24; i++){
    var select = document.getElementById("select-form3");
    var option = document.createElement("OPTION");
    select.options.add(option);
    option.text = i;
    option.value = i;
}