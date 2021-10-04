package jp.co.ogis_ri.nautible.app.stock.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

/**
 * 在庫のサービスクラス
 */
@ApplicationScoped
public class StockService {

    Logger LOG = Logger.getLogger(StockService.class.getName());

    @Inject
    Instance<StockRepository> stockRepository;
    @Inject
    Instance<StockAllocateHistoryRepository> stockAllocateHistoryRepository;
    @Inject
    StockSagaManager stockSagaManager;

    /**
     * 在庫を取得する
     * @param id Id
     * @return {@link Stock}
     */
    public Stock getByStockId(int id) {
        return stockRepository.get().getByStockId(id);
    }

    /**
     * 在庫を取得する
     * @param id 商品id
     * @return {@link Stock}
     */
    public Stock getByProductId(int id) {
        return stockRepository.get().getByProductId(id);
    }

    /**
     * 全在庫を取得する
     * @return 在庫のリスト
     */
    public List<Stock> findAll() {
        return stockRepository.get().find();
    }

    /**
     * 在庫を作成する
     * @param stock 在庫
     * @return {@link Stock}
     */
    public Stock create(Stock stock) {
        return stockRepository.get().create(stock);
    }

    /**
     * 在庫を更新する
     * @param stock 在庫
     * @return {@link Stock}
     */
    public Stock update(Stock stock) {
        return stockRepository.get().update(stock);
    }

    /**
     * 在庫を削除する
     * @param id Id
     */
    public void deleteByStockId(int id) {
        stockRepository.get().delete(id);
    }

    /**
     * SAGA。在庫の引当予約を行う（数量を減らして、予約数量を増やす）。PENDINGステータスで引当履歴を作成する（Semantic Lock カウンターメジャー）。<br>
     * 引当タイミング（日時）や引当の起因となる受注の管理等は考慮しない。
     * @param requestId リクストId
     * @param allocateProducts 商品Id、数量
     */
    public void reserveAllocate(String requestId, List<Pair<Integer, Integer>> allocateProducts) {
        boolean rejected = false;
        List<StockAllocateHistory> allocatedHistories = new ArrayList<>();

        // 引当の予約と履歴の作成
        Function<Pair<Integer, Integer>, StockAllocateHistory> allocateAndCreateHistory = (allocateProduct) -> {
            Stock stock = stockRepository.get().getByProductId(allocateProduct.getLeft());
            StockAllocateHistory history = stock.reserveAllocate(requestId, allocateProduct.getRight());
            return history;
        };

        //REJECTの履歴だけ作成
        Function<Pair<Integer, Integer>, StockAllocateHistory> createRejectHistory = (allocateProduct) -> {
            return stockAllocateHistoryRepository.get()
                    .create(new StockAllocateHistory().requestId(requestId).productId(allocateProduct.getLeft())
                            .status(AllocateStatus.REJECTED));
        };

        for (Pair<Integer, Integer> allocateProduct : allocateProducts) {
            StockAllocateHistory history = rejected ? createRejectHistory.apply(allocateProduct)
                    : allocateAndCreateHistory.apply(allocateProduct);
            // 在庫不足によりREJECT
            if (history.getStatus() == AllocateStatus.REJECTED) {
                rejected = true;
            } else {
                allocatedHistories.add(history);
            }
        }

        if (rejected == false) {
            // 正常終了
            // pubsub normal reply
            stockSagaManager.replyReserveAllocate(requestId);
            return;
        }

        // 在庫不足が発生した場合

        // 既に処理が完了してしまったものをREJECT
        allocatedHistories.forEach(h -> {
            Stock stock = stockRepository.get().getByProductId(h.getProductId());
            stock.rejectAllocate(h);
        });

        stockSagaManager.replyReserveAllocateBadRequest(requestId, "Lack of stock");

    }

    /**
     * SAGA。引当予約を承認する。引当処理を実施し、引当履歴のステータスを引当済みに変更する（Semantic Lock カウンターメジャー）
     * 引当タイミング（日時）や引当の起因となる受注の管理等は考慮しない。
     * @param requestId リクエストId
     * @return キャンセルリクエストId
     */
    public void approveAllocate(String requestId) {
        List<StockAllocateHistory> stockAllocateHistorys = stockAllocateHistoryRepository
                .get().getStockAllocateHistoryByRequestId(requestId);
        stockAllocateHistorys.stream().forEach(h -> {
            Stock stock = stockRepository.get().getByProductId(h.getProductId());
            stock.approveAllocate(h);
        });
        stockSagaManager.replyApproveAllocate(requestId);
    }

    /**
     * SAGA。引当予約を却下する。引当予約を取り消す（数量を増やして、予約数量を減らす）。引当履歴のステータスを却下に変更する（Semantic Lock カウンターメジャー）
     * @param requestId リクエストId
     * @return キャンセルリクエストId
     */
    public void rejectAllocate(String requestId) {
        List<StockAllocateHistory> stockAllocateHistorys = stockAllocateHistoryRepository.get()
                .getStockAllocateHistoryByRequestId(requestId);
        stockAllocateHistorys.stream().forEach(h -> {
            Stock stock = stockRepository.get().getByProductId(h.getProductId());
            stock.rejectAllocate(h);
        });
        stockSagaManager.rejectReserveAllocate(requestId);
    }

    /**
     * 引当予約のリクエスト不正の応答を返す
     * @param requestId リクエストID
     * @param message メッセージ
     */
    public void replyReserveAllocateBadRequest(String requestId, String message) {
        stockSagaManager.replyReserveAllocateBadRequest(requestId, message);
    }

    /**
     * 引当承認のリクエスト不正の応答を返す
     * @param requestId リクエストID
     * @param message メッセージ
     */
    public void replyApproveAllocateBadRequest(String requestId, String message) {
        stockSagaManager.replyApproveAllocateBadRequest(requestId, message);
    }

    /**
     * 引当却下のリクエスト不正の応答を返す
     * @param requestId リクエストID
     * @param message メッセージ
     */
    public void replyRejectAllocateBadRequest(String requestId, String message) {
        stockSagaManager.replyRejectAllocateBadRequest(requestId, message);
    }

}
