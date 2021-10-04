package jp.co.ogis_ri.nautible.app.stock.domain;

import java.util.Objects;

import javax.enterprise.inject.spi.CDI;

import jp.co.ogis_ri.nautible.app.stock.domain.StockRepository.StockAllocateResult;

/**
 * 在庫ドメイン
 */
public class Stock {
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
     * @return {@link Stock}
     */
    public Stock id(Integer id) {
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
     * @return {@link Stock}
     */
    public Stock productId(Integer productId) {
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
     * @return {@link Stock}
     */
    public Stock quantity(Integer quantity) {
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
     * @return {@link Stock}
     */
    public Stock reservedQuantity(Integer reservedQuantity) {
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
        Stock stock = (Stock) o;
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

    /**
     * 在庫の引当予約を行う（数量を減らして、予約数量を増やす）。PENDINGステータスで引当履歴を作成する（Semantic Lock カウンターメジャー）。<br>
     * 引当タイミング（日時）や引当の起因となる受注の管理等は考慮しない。
     * @param requestId リクストId
     * @param quantity 数量
     * @return {@link StockAllocateHistory}
     */
    public StockAllocateHistory reserveAllocate(String requestId, int quantity) {
        StockRepository stockRepository = getStockRepository();
        StockAllocateResult stockAllocateResult = stockRepository.allocate(
                new StockAllocateHistory().requestId(requestId).productId(getProductId()).quantity(quantity));
        quantity(stockAllocateResult.getStockQuantity())
                .reservedQuantity(stockAllocateResult.getStockReservedQuantity());
        return stockAllocateResult.getStockAllocateHistory();
    }

    /**
     * 引当予約を承認する。引当処理を実施し、引当履歴のステータスを引当済みに変更する（Semantic Lock カウンターメジャー）
     * 引当タイミング（日時）や引当の起因となる受注の管理等は考慮しない。
     * @param stockAllocateHistory {@link StockAllocateHistory}
     * @return {@link StockAllocateHistory}
     */
    public StockAllocateHistory approveAllocate(StockAllocateHistory stockAllocateHistory) {
        StockRepository stockRepository = getStockRepository();
        stockAllocateHistory.approve();
        StockAllocateResult stockAllocateResult = stockRepository.allocate(stockAllocateHistory);
        quantity(stockAllocateResult.getStockQuantity())
                .reservedQuantity(stockAllocateResult.getStockReservedQuantity());
        return stockAllocateHistory;
    }

    /**
     * 引当予約を却下する。引当予約を取り消す（数量を増やして、予約数量を減らす）。引当履歴のステータスを却下に変更する（Semantic Lock カウンターメジャー）
     * @param stockAllocateHistory {@link StockAllocateHistory}
     * @return {@link StockAllocateHistory}
     */
    public StockAllocateHistory rejectAllocate(StockAllocateHistory stockAllocateHistory) {
        StockRepository stockRepository = getStockRepository();
        stockAllocateHistory.reject();
        StockAllocateResult stockAllocateResult = stockRepository.allocate(stockAllocateHistory);
        quantity(stockAllocateResult.getStockQuantity())
                .reservedQuantity(stockAllocateResult.getStockReservedQuantity());
        return stockAllocateHistory;
    }

    /**
     * CDIから {@link StockRepository}を取得する
     * @return {@link StockRepository}
     */
    private StockRepository getStockRepository() {
        return CDI.current().select(StockRepository.class).get();
    }

}
