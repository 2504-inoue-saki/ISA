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

     // ユーザー停止状態プルダウン変更時にボタンを活性化するロジック
    $('.select-box').on('change', function() {
        var $form = $(this).closest('form');
        var $button = $form.find('.update-isStopped-button');
        $button.prop('disabled', false);
    });

    // 「変更」ボタンクリック時に確認ダイアログを表示するロジック
    $('.update-isStopped-button').on('click', function() {
        if (confirm($(this).parents('.switch-isStopped').find('input[name="name"]').val() + "を" + $(this).parents('.switch-isStopped').find('option:selected').text() + "状態にします。よろしいですか？")) {
            return true; // OKならフォームを送信
        }
        return false; // キャンセルならフォーム送信を中止
    });

    // アカウント削除ボタン押下時の処理
    	$('.delete').on('click', function() {
    		if (confirm("削除します。よろしいですか?")) {
    			return true;
    		}
    		return false;
    	});
});