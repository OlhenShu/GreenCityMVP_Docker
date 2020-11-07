var languages;
$.get('/factoftheday/languages',function (data){
    languages=data;
});

function clearAllErrorsSpan(){
    $('.errorSpan').text('');
}

$(document).ready(function(){
    // Activate tooltip
    $('[data-toggle="tooltip"]').tooltip();

    // Select/Deselect checkboxes
    var checkbox = $('table tbody input[type="checkbox"]');
    $("#selectAll").click(function(){
        if(this.checked){
            checkbox.each(function(){
                this.checked = true;
            });
        } else{
            checkbox.each(function(){
                this.checked = false;
            });
        }
    });
    checkbox.click(function(){
        if(!this.checked){
            $("#selectAll").prop("checked", false);
        }
    });
    //Кнопка edit справа в таблиці
    $('td .edit.eBtn').on('click',function(event){
        event.preventDefault();
        $("#editHabitFactsModal").each(function(){
            $(this).find('input.eEdit').val("");
        });
        clearAllErrorsSpan();
        $('#editHabitFactsModal').modal();
        var href = $(this).attr('href');
        $.get(href, function (habitfacts){
            $('#id').val(habitfacts.id);
            $('#habit_id').val(habitfacts.habit.id);
            habitfacts.translations.forEach(function (translation){
                let lang = translation.language.code;
                $(`input[name=content${lang}]`).val(translation.content);
                $(`input[name=status${lang}]`).val(translation.factOfDayStatus);
            });
        });
    });
    //Кнопка addHabitFacts зверху таблиці
    $('#addHabitFactsModalBtn').on('click',function(event){
        clearAllErrorsSpan();
    });
    //Кнопка delete справа в таблиці
    $('td .delete.eDelBtn').on('click',function(event){
        event.preventDefault();
        $('#deleteHabitFactsModal').modal();
        var href = $(this).attr('href');
        $('#deleteOneSubmit').attr('href',href);
    });
    //Кнопка delete в deleteHabitFactsModal
    $('#deleteOneSubmit').on('click',function(event){
        event.preventDefault();
        var href = $(this).attr('href');
        $.ajax({
            url: href,
            type: 'delete',
            dataType: 'json',
            contentType: 'application/json',
            success: function (data) {
                location.reload();
            },
        });
    });
    //Кнопка delete в deleteAllSelectedModal
    $('#deleteAllSubmit').on('click',function(event){
        event.preventDefault();
        var payload=[];
        checkbox.each(function (){
            if(this.checked){
                payload.push(this.value);
            }
        });
        var href = '/management/facts/deleteAll';
        $.ajax({
            url: href,
            type: 'delete',
            dataType: 'json',
            contentType: 'application/json',
            success: function (data) {
                location.reload();
            },
            data: JSON.stringify(payload)
        });
    });
    //Кнопка submit в модальній формі Add
    $('#submitAddBtn').on('click',function(event){
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = $('#addHabitFactsForm').serializeArray().reduce(function(obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var payload={
            "habit" : {
                "id" : formData.habit_id
            },
            "translations" : []
        };
        for (var key in formData) {
            var lang, langId;
            if (key.startsWith("content")) {
                lang = key.split("content").pop();
                if (lang === 'ua') {
                    langId = 1;
                } else if (lang === 'en') {
                    langId = 2;
                } else if (lang === 'ru') {
                    langId = 3;
                }
                payload.translations.push(
                    {
                        "content": formData["content" + lang],
                        "language": {
                            "code": lang,
                            "id" : langId
                        }
                    }
                );
            }
        }
        //запит save у модальній формі add
        $.ajax({
            url: '/management/facts/',
            type: 'post',
            dataType: 'json',
            contentType: 'application/json',
            success: function (data) {
                if(Array.isArray(data.errors) && data.errors.length){
                    data.errors.forEach(function(el){
                        $(document.getElementById('errorModalSave'+el.fieldName)).text(el.fieldError);
                    })
                }
                else{
                    location.reload();
                }
            },
            data: JSON.stringify(payload)
        });
    })

    //Кнопка submit в модальній формі Edit
    $('#submitEditBtn').on('click',function(event){
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = $('#editHabitFactsForm').serializeArray().reduce(function(obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var returnData={
            "id" : formData.id,
            "habit" : {
                "id" : formData.habit_id
            },
            "translations" : [
            ]
        };
        for (var key in formData) {
            var lang, langId;
            if (key.startsWith("content")) {
                lang = key.split("content").pop();
                if (lang === 'ua') {
                    langId = 1;
                } else if (lang === 'en') {
                    langId = 2;
                } else if (lang === 'ru') {
                    langId = 3;
                }
                returnData.translations.push(
                    {
                        "content": formData["content" + lang],
                        "language": {
                            "code": lang,
                            "id" : langId
                        },
                        "factOfDayStatus" : formData["status" + lang]
                    }
                );
            }
        }
        //запит save у модальній формі update
        $.ajax({
            url: '/management/facts/'+returnData.id,
            type: 'put',
            dataType: 'json',
            contentType: 'application/json',
            success: function (data) {
                if(Array.isArray(data.errors) && data.errors.length){
                    data.errors.forEach(function(el){
                        $(document.getElementById('errorModalUpdate'+el.fieldName)).text(el.fieldError);
                    })
                }
                else{
                    location.reload();
                }
            },
            data: JSON.stringify(returnData)
        });
    })


});
