package jp.co.ogis_ri.nautible.app.stock.domain;

import java.util.List;

/**
 * 在庫引当履歴レポジトリ
 */
public interface StockAllocateHistoryRepository {
    /**
     * 引当履歴を作成する
     * @param stockAllocateHistory 引当履歴
     * @return {@link StockAllocateHistory}
     */
    StockAllocateHistory create(StockAllocateHistory stockAllocateHistory);

    /**
     * 引当履歴を取得する
     * @param requestId リクエストId
     * @return {@link StockAllocateHistory}
     */
    List<StockAllocateHistory> getStockAllocateHistoryByRequestId(String requestId);

    /**
     * 引当履歴のステータスを更新する
     * @param requestId リクエストId
     * @return {@link StockAllocateHistory}
     */
    public StockAllocateHistory updateStatus(StockAllocateHistory stockAllocateHistory);

}
