let maxDate = new Date();
maxDate.setMonth(maxDate.getMonth() + 3);

flatpickr("#orderDate", {
    locale: "ja", // 日本語化
    dateFormat: "Y-m-d", // 年-月-日の形式
    minDate: "today", // 今日以降の日付のみ選択可能
    maxDate: new Date(maxDate), // 3ヶ月先の日付まで選択可能
    onChange: function(selectedDates) {
        document.getElementById('reservationDate').value = selectedDates[0].toISOString().split('T')[0];
    }
});

 flatpickr("#orderTime", {
    enableTime: true,
    noCalendar: true,
    time_24hr: true, // 24時間表示
    dateFormat: "H:i", // 時:分の形式
    onChange: function(selectedTimes) {
        document.getElementById('reservationTime').value = selectedTimes[0].toLocaleTimeString();
    }
});