window.notify = function (message) {
    $.notify(message, {
        position: "right bottom",
        className: "success"
    });
}

window.error = function (message) {
    $.notify(message, {
        position: "right bottom",
        className: "error"
    })
}

errorFunction = error => function (response) {
    if (response["error"]) {
        error.text(response["error"]);
    }
}

window.ajaxRegisterOrEnter = function (data, error) {
    ajax(data, errorFunction(error), true, false)
}

window.ajax = function (data, successFunction, redirect= true,
                        catchErrors=true,
                        type = "POST", url = "", dataType = "json") {
    $.ajax({
        type: type,
        url: url,
        dataType: dataType,
        data,
        success: function (response) {
            if (redirect && response["redirect"]) {
                location.href = response["redirect"];
            } else if (catchErrors && response["error"]) {
                window.error(response["error"])
            } else {
                successFunction(response)
            }
        }
    });
}
