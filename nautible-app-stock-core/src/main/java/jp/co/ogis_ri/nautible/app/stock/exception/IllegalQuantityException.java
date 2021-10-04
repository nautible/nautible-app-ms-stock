package jp.co.ogis_ri.nautible.app.stock.exception;

/**
 * 在庫引当数量不正例外。数量超過で引当できない場合の例外。実案件の場合は例外の構造（階層）の検討要。
 */
public class IllegalQuantityException extends Exception {

    /**
     * コンストラクタ
     * @param message メッセージ
     * @param cause cause
     */
    public IllegalQuantityException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * コンストラクタ
     * @param message メッセージ
     */
    public IllegalQuantityException(String message) {
        super(message);
    }

}
