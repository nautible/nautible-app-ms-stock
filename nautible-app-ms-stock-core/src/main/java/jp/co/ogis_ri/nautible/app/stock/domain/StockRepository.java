package jp.co.ogis_ri.nautible.app.stock.domain;

import java.util.List;

/**
 * 在庫レポジトリ
 */
public interface StockRepository {
    /**
     * 在庫を取得する
     * @param id Id
     * @return {@link Stock}
     */
    Stock getByStockId(int id);

    /**
     * 在庫を取得する
     * @param id Id
     * @return {@link Stock}
     */
    Stock getByProductId(int id);

    /**
     * 全在庫を取得する
     * @return 在庫のリスト
     */
    List<Stock> find();

    /**
     * 在庫を作成する
     * @param stock 在庫
     * @return {@link Stock}
     */
    Stock create(Stock stock);

    /**
     * 在庫を削除する
     * @param id Id
     */
    void delete(int id);

    /**
     * 在庫を更新する
     * @param stock 在庫
     * @return {@link Stock}
     */
    Stock update(Stock stock);

    /**
     * 引当履歴をもとに在庫を引当行う。
     * <li>予約の場合は、数量を減らし、予約数量を増やす。
     * <li>承認の場合は、予約数量を減らす。
     * <li>却下の場合は、数量を増やし、予約数量を減らす。
     * 数量を増やし、予約数量を減らす。
     * @param stockAllocateHistory 対象の引当履歴
     * @return {@link StockAllocateResult}
     */
    StockAllocateResult allocate(StockAllocateHistory stockAllocateHistory);

    //    /**
    //     * 引当履歴を作成する
    //     * @param stockAllocateHistory 引当履歴
    //     * @return {@link StockAllocateHistory}
    //     */
    //    StockAllocateHistory create(StockAllocateHistory stockAllocateHistory);
    //
    //    /**
    //     * 引当履歴を取得する
    //     * @param requestId リクエストId
    //     * @return {@link StockAllocateHistory}
    //     */
    //    List<StockAllocateHistory> getStockAllocateHistoryByRequestId(String requestId);

    /**
     * 在庫引当結果
     */
    class StockAllocateResult {
        /** 引当履歴 */
        private StockAllocateHistory stockAllocateHistory;
        /** 在庫数量 */
        private int stockQuantity;
        /**
         * 在庫予約数量
         **/
        private int stockReservedQuantity;

        /**
         * 引当履歴を取得する
         * @return {@link StockAllocateHistory}
         */
        public StockAllocateHistory getStockAllocateHistory() {
            return stockAllocateHistory;
        }

        /**
         * 引当履歴を設定する
         * @param stockAllocateHistory 引当履歴
         */
        public void setStockAllocateHistory(StockAllocateHistory stockAllocateHistory) {
            this.stockAllocateHistory = stockAllocateHistory;
        }

        /**
         * 引当履歴を設定する
         * @param stockAllocateHistory 引当履歴
         * @return {@link StockAllocateResult}
         */
        public StockAllocateResult stockAllocateHistory(StockAllocateHistory stockAllocateHistory) {
            this.stockAllocateHistory = stockAllocateHistory;
            return this;
        }

        /**
         * 在庫数量を取得する
         * @return 在庫数量
         */
        public Integer getStockQuantity() {
            return stockQuantity;
        }

        /**
         * 在庫数量を設定する
         * @return 在庫数量
         */
        public void setStockQuantity(int stockQuantity) {
            this.stockQuantity = stockQuantity;
        }

        /**
         * 在庫数量を設定する
         * @return 在庫数量
         * @return {@link StockAllocateResult}
         */
        public StockAllocateResult stockQuantity(int stockQuantity) {
            this.stockQuantity = stockQuantity;
            return this;
        }

        /**
         * 在庫予約数量を取得する
         * @return 在庫予約数量
         */
        public Integer getStockReservedQuantity() {
            return stockReservedQuantity;
        }

        /**
         * 在庫予約数量を設定する
         * @return 在庫予約数量
         */
        public void setStockReservedQuantity(int stockReservedQuantity) {
            this.stockReservedQuantity = stockReservedQuantity;
        }

        /**
         * 在庫予約数量を設定する
         * @return 在庫予約数量
         * @return {@link StockAllocateResult}
         */
        public StockAllocateResult stockReservedQuantity(int stockReservedQuantity) {
            this.stockReservedQuantity = stockReservedQuantity;
            return this;
        }

    }
}
