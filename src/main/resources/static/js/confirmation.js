$(document).ready(function() {

    // 承認状況を変更するとボタンを活性化するロジック
    $('.select-box').on('change', function() {
        var $form = $(this).closest('form');
        var $button = $form.find('.submit-approval');
        $button.prop('disabled', false);
    });

    // 「変更」ボタンクリック時に確認ダイアログを表示するロジック
    $('.submit-approval').on('click', function() {
       if (confirm($(this).parents('.switch-approval').find('input[name="date"]').val() + "分を" + $(this).parents('.switch-approval').find('option:selected').text() + "にします。よろしいですか？")) {
          return true; // OKならフォームを送信
       }
       return false; // キャンセルならフォーム送信を中止
    });
});