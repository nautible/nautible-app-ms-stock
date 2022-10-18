package jp.co.ogis_ri.nautible.app.stock.outbound.cosmosdb;

import java.util.Objects;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import io.quarkus.mongodb.panache.common.MongoEntity;

/**
 * 在庫ドメイン
 */
@MongoEntity(collection = "Stock")
public class CosmosdbStock {
    /** Id */
    @BsonId // メソッドに定義すると有効にならないAPIがある。
    private Integer id = null;
    /** 商品Id */
    @BsonProperty("ProductId") // メソッドに定義すると有効にならないAPIがある。
    private Integer productId = null;
    /** 数量（在庫） */
    @BsonProperty("Quantity")
    private Integer quantity = null;
    /**
     * 予約数量（引当がPENDINGステータスの数量）
     **/
    @BsonProperty("ReservedQuantity")
    private Integer reservedQuantity = null;

    /**
     * Idを設定する
     * @param id Id
     * @return {@link CosmosdbStock}
     */
    public CosmosdbStock id(Integer id) {
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
     * 商品Idを設定する
     * @param productId 商品Id
     * @return {@link CosmosdbStock}
     */
    public CosmosdbStock productId(Integer productId) {
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
     * 商品Idを設定する
     * @param productId 商品Id
     */
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    /**
     * 数量を設定する
     * @param quantity 数量
     * @return {@link CosmosdbStock}
     */
    public CosmosdbStock quantity(Integer quantity) {
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
     * 予約数量を設定する
     * @param reservedQuantity 予約数量
     * @return {@link CosmosdbStock}
     */
    public CosmosdbStock reservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
        return this;
    }

    /**
     * 予約数量を取得する
     * @return 予約数量
     */
    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    /**
     * 予約数量を設定する
     * @param reservedQuantity 予約数量
     */
    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CosmosdbStock stock = (CosmosdbStock) o;
        return Objects.equals(this.id, stock.id) &&
                Objects.equals(this.productId, stock.productId) &&
                Objects.equals(this.reservedQuantity, stock.reservedQuantity) &&
                Objects.equals(this.quantity, stock.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productId, quantity);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RestStock {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    productId: ").append(toIndentedString(productId)).append("\n");
        sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
        sb.append("    reservedQuantity: ").append(toIndentedString(reservedQuantity)).append("\n");
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
