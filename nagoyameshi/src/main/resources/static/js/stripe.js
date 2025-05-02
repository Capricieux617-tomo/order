const stripe = Stripe('pk_test_51QIgjwDQ69lR6qbPACVOLmbs1rj0u8EyrtLWv63pDU8md0sGeBlOzolmXySoURYgJAHvZ8ocaQsuAmqaWZyCRtfP00C4yoxgtx'); // 公開鍵

const paymentButton = document.querySelector('#paymentButton');

if (paymentButton) { // paymentButtonが存在する場合のみ実行
    paymentButton.addEventListener('click', () => {
		
        // sessionIdがnullか未定義の場合のチェック
        if (!window.sessionId) {
            console.error('Session ID is null or undefined.');
            alert('決済処理を開始できませんでした。再度お試しください。');
            return;
        }

        // ボタンを無効化して、処理中の表示に変更
        paymentButton.disabled = true;
        paymentButton.textContent = '処理中...';

        // Stripe Checkoutにリダイレクト
        stripe.redirectToCheckout({ sessionId: window.sessionId })
            .then(function (result) {
                if (result.error) {
                    // 決済処理エラーが発生した場合
                    console.error('Stripe checkout error:', result.error);
                    alert('決済処理に失敗しました。詳細: ' + result.error.message);
                }
            })
            .catch(function (error) {
                // 決済処理中にエラーが発生した場合
                console.error('Error occurred during Stripe checkout:', error);
                alert('決済処理中にエラーが発生しました。再試行してください。');
            })
            .finally(() => {
                // ボタンを元に戻す（再度有効化）
                paymentButton.disabled = false;
                paymentButton.textContent = '支払い';
            });
    });
}
