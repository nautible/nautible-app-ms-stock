package jp.co.ogis_ri.nautible.app.stock.exception;

/**
 * MessageBrokerを利用した場合などの、重複処理要求を表す例外。冪等性の担保に利用する。
 */
public class DuplicateRequestException extends RuntimeException {

    /**
     * コンストラクタ
     * @param message メッセージ
     * @param cause cause
     */
    public DuplicateRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * コンストラクタ
     * @param message メッセージ
     */
    public DuplicateRequestException(String message) {
        super(message);
    }

    /**
     * コンストラクタ
     * @param cause cause
     */
    public DuplicateRequestException(Throwable cause) {
        super(cause);
    }

}
