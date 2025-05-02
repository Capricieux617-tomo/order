<script>
document.addEventListener('DOMContentLoaded', function() {
    const existingCategoryRadio = document.getElementById('existingCategory');
    const newCategoryRadio = document.getElementById('newCategory');
    const existingCategoryOptions = document.getElementById('existingCategoryOptions');
    const newCategoryOptions = document.getElementById('newCategoryOptions');

    function toggleCategoryOptions() {
        if (existingCategoryRadio.checked) {
            existingCategoryOptions.style.display = 'block';
            newCategoryOptions.style.display = 'none';
        } else {
            existingCategoryOptions.style.display = 'none';
            newCategoryOptions.style.display = 'block';
        }
    }

    existingCategoryRadio.addEventListener('change', toggleCategoryOptions);
    newCategoryRadio.addEventListener('change', toggleCategoryOptions);

    // 初回実行
    toggleCategoryOptions();
});
</script>
