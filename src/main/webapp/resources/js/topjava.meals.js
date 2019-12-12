const URL_GET       = "ajax/profile/meals/";
const URL_FILTER    = "ajax/profile/meals/filter";

const DATE_TIME_FORMAT  = "Y-m-d H:i";
const DATE_FORMAT       = "Y-m-d";
const TIME_FORMAT       = "H:i";

function updateFilteredTable() {
    $.ajax({
        type: "GET",
        url: URL_FILTER,
        data: $("#filter").serialize()
    }).done(updateTableByData);
}

function clearFilter() {
    $("#filter")[0].reset();
    $.get(URL_GET, updateTableByData);
}

$(function () {
    makeEditable({
        ajaxUrl: URL_GET,
        datatableApi: $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime"
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "desc"
                ]
            ]
        }),
        updateTable: updateFilteredTable
    });

    // init input fields for date-time, dates, times
    $("#dateTime").datetimepicker({
        format: DATE_TIME_FORMAT
    });
    $("#filter #startDate").datetimepicker({
        format: DATE_FORMAT,
        timepicker: false
    });
    $("#filter #endDate").datetimepicker({
        format: DATE_FORMAT,
        timepicker: false
    });
    $("#filter #startTime").datetimepicker({
        format: TIME_FORMAT,
        datepicker: false
    });
    $("#filter #endTime").datetimepicker({
        format: TIME_FORMAT,
        datepicker: false
    });
});