package jp.co.ogis_ri.nautible.app.stock.outbound.cosmosdb;

import java.util.Objects;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import io.quarkus.mongodb.panache.MongoEntity;

/**
 * 在庫引当履歴<br>
 *  引当ステータスはSemantic Lock カウンターメジャー
 */
@MongoEntity(collection = "StockAllocateHistory")
public class CosmosdbStockAllocateHistory {
    /** Id */
    @BsonId // メソッドに定義すると有効にならないAPIがある。
    private Integer id = null;
    /** リクエストIｄ。処理要求を一意に表す。 */
    @BsonProperty("RequestId") // メソッドに定義すると有効にならないAPIがある。
    private String requestId = null;
    /** 商品Id */
    @BsonProperty("ProductId")
    private Integer productId = null;
    /** 数量 */
    @BsonProperty("Quantity")
    private Integer quantity = null;
    /** 割り当てステータス */
    @BsonProperty("Status")
    private CosmosdbAllocateStatus status = CosmosdbAllocateStatus.PENDING;
    /** メモ */
    @BsonProperty("Memo")
    private String memo = null;

    /**
     * Idを設定する
     * @param id Id
     * @return {@link CosmosdbStockAllocateHistory}
     */
    public CosmosdbStockAllocateHistory id(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Idを取得する
     * @return Id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Idを設定する
     * @param id Id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * リクエストIdを設定する
     * @param requestId リクエストId
     * @return {@link CosmosdbStockAllocateHistory}
     */
    public CosmosdbStockAllocateHistory requestId(String requestId) {
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
     * @return {@link CosmosdbStockAllocateHistory}
     */
    public CosmosdbStockAllocateHistory productId(Integer productId) {
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
     * @return {@link CosmosdbStockAllocateHistory}
     */
    public CosmosdbStockAllocateHistory quantity(Integer quantity) {
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
     * @return {@link CosmosdbStockAllocateHistory}
     */
    public CosmosdbStockAllocateHistory status(CosmosdbAllocateStatus status) {
        this.status = status;
        return this;
    }

    /**
     * ステータスを取得する
     * @return {@link CosmosdbAllocateStatus}
     */
    public CosmosdbAllocateStatus getStatus() {
        return status;
    }

    /**
     * ステータスを設定する
     * @param status ステータス
     */
    public void setStatus(CosmosdbAllocateStatus status) {
        this.status = status;
    }

    /**
     * メモを変更する
     * @param memo メモ
     * @return {@link CosmosdbStockAllocateHistory}
     */
    public CosmosdbStockAllocateHistory memo(String memo) {
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
        CosmosdbStockAllocateHistory stockAllocateHistory = (CosmosdbStockAllocateHistory) o;
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

}
