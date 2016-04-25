/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var sendJson;

function send() {

    $.ajaxSetup({
        headers: {'Authorization': 'Basic cm9vdDpXYzdtaXVacEE2'}
    });

    sendJson = [{"id": "5", "level": 1, "delayTrigger": 120, "routeId": "2", "handlers": ["twitter"]}];

    $.ajax({
        type: "POST",
        url: "http://verkeer-2.bp.tiwi.be/api/v2/thresholds",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: JSON.stringify(sendJson),
        success: successCall,
        error: failedCall
    });

}

function successCall(data) {

    alert("OK");
}

function failedCall(data) {

    alert("Fail");
}