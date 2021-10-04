package jp.co.ogis_ri.nautible.app.stock.outbound.dynamodb;

import java.util.Objects;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

/**
 * 在庫ドメイン
 */
@DynamoDbBean
public class DynamodbStock {
    /** Id */
    private Integer id = null;
    /** 商品Id */
    private Integer productId = null;
    /** 数量（在庫） */
    private Integer quantity = null;
    /**
     * 予約数量（引当がPENDINGステータスの数量）
     **/
    private Integer reservedQuantity = null;

    /**
     * 数量を設定する
     * @param quantity 数量
     * @return {@link DynamodbStock}
     */
    public DynamodbStock id(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Idを取得する
     * @return Id
     */
    @DynamoDbPartitionKey
    @DynamoDbAttribute("Id")
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
     * @return {@link DynamodbStock}
     */
    public DynamodbStock productId(Integer productId) {
        this.productId = productId;
        return this;
    }

    /**
     * 商品Idを取得する
     * @return 商品Id
     */
    @DynamoDbAttribute("ProductId")
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
     * @return {@link DynamodbStock}
     */
    public DynamodbStock quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    /**
     * 数量を取得する
     * @return 数量
     */
    @DynamoDbAttribute("Quantity")
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
     * @return {@link DynamodbStock}
     */
    public DynamodbStock reservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
        return this;
    }

    /**
     * 予約数量を取得する
     * @return 予約数量
     */
    @DynamoDbAttribute("ReservedQuantity")
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
        DynamodbStock stock = (DynamodbStock) o;
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
