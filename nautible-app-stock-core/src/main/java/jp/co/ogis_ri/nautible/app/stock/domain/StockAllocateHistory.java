package jp.co.ogis_ri.nautible.app.stock.domain;

import java.util.Objects;
import java.util.Set;

/**
 * 在庫引当履歴<br>
 *  引当ステータスはSemantic Lock カウンターメジャー
 */
public class StockAllocateHistory {
    /** リクエストIｄ。処理要求を一意に表す。 */
    private String requestId = null;
    /** 商品Id */
    private Integer productId = null;
    /** 数量 */
    private Integer quantity = null;
    /** 割り当てステータス */
    private AllocateStatus status = AllocateStatus.PENDING;
    /** メモ */
    private String memo = null;

    /**
     * リクエストIdを設定する
     * @param requestId リクエストId
     * @return {@link StockAllocateHistory}
     */
    public StockAllocateHistory requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * リクエストIdを取得する
     * @return リクエストId
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * リクエストIdを設定する
     * @param requestId リクエストId
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * 商品Idを設定する
     * @param productId 商品Id
     * @return {@link StockAllocateHistory}
     */
    public StockAllocateHistory productId(Integer productId) {
        this.productId = productId;
        return this;
    }

    /**
     * 商品Idを取得する
     * @return 商品Id
     */
    public Integer getProductId() {
        return productId;
    }

    /**
     * 商品Idを取得する
     * @param productId 商品Id
     */
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    /**
     * 数量を設定する
     * @param quantity 数量
     * @return {@link StockAllocateHistory}
     */
    public StockAllocateHistory quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    /**
     * 数量を取得する
     * @return 数量
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * 数量を設定する
     * @param quantity 数量
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * ステータスを変更する
     * @param status ステータス
     * @return {@link StockAllocateHistory}
     */
    public StockAllocateHistory status(AllocateStatus status) {
        this.status = status;
        return this;
    }

    /**
     * ステータスを取得する
     * @return {@link AllocateStatus}
     */
    public AllocateStatus getStatus() {
        return status;
    }

    /**
     * ステータスを設定する
     * @param status ステータス
     */
    public void setStatus(AllocateStatus status) {
        this.status = status;
    }

    /**
     * メモを変更する
     * @param memo メモ
     * @return {@link StockAllocateHistory}
     */
    public StockAllocateHistory memo(String memo) {
        this.memo = memo;
        return this;
    }

    /**
     * メモを取得する
     * @return メモ
     */
    public String getMemo() {
        return memo;
    }

    /**
     * メモを設定する
     * @param comment メモ
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StockAllocateHistory stockAllocateHistory = (StockAllocateHistory) o;
        return Objects.equals(this.requestId, stockAllocateHistory.requestId) &&
                Objects.equals(this.productId, stockAllocateHistory.productId) &&
                Objects.equals(this.status, stockAllocateHistory.status) &&
                Objects.equals(this.memo, stockAllocateHistory.memo) &&
                Objects.equals(this.quantity, stockAllocateHistory.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, productId, quantity);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class StockAllocateHistory {\n");
        sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
        sb.append("    productId: ").append(toIndentedString(productId)).append("\n");
        sb.append("    status: ").append(status).append("\n");
        sb.append("    memo: ").append(memo).append("\n");
        sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    /**
     * 許可。ステータスを引き当て済みにする。
     */
    public void approve() {
        this.status = AllocateStatus.ALLOCATED;
    }

    /**
     * 拒否
     */
    public void reject() {
        this.status = AllocateStatus.REJECTED;
    }

    /**
     * ステータス更新時の変更前ステータス（前提条件）を取得する。冪等性チェックで利用する。
     * @return 変更前ステータス（前提条件）
     */
    public Set<AllocateStatus> getPreconditionStatusWhenChangeStatus() {
        switch (getStatus()) {
        case ALLOCATED:
            return Set.of(AllocateStatus.PENDING);
        case REJECTED:
            return Set.of(AllocateStatus.PENDING, AllocateStatus.ALLOCATED);
        default:
            // PENDING
            return null;
        }
    }
}
