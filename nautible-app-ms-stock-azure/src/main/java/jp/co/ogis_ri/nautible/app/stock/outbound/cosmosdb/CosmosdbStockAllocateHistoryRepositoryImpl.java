package jp.co.ogis_ri.nautible.app.stock.outbound.cosmosdb;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.bson.Document;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jp.co.ogis_ri.nautible.app.stock.domain.AllocateStatus;
import jp.co.ogis_ri.nautible.app.stock.domain.StockAllocateHistory;
import jp.co.ogis_ri.nautible.app.stock.domain.StockAllocateHistoryRepository;
import jp.co.ogis_ri.nautible.app.stock.exception.DuplicateRequestException;

/**
 * 在庫引当履歴レポジトリ
 */
@ApplicationScoped
public class CosmosdbStockAllocateHistoryRepositoryImpl
        implements StockAllocateHistoryRepository, PanacheMongoRepositoryBase<CosmosdbStockAllocateHistory, Integer> {
    /** 在庫引当履歴テーブル名 */
    private static final String STOCK_ALLOCATE_HISTORY_TABLE_NAME = "StockAllocateHistory";
    @Inject
    MongoClient mongoClient;
    @Inject
    CosmosdbStockAllocateHistoryMapper cosmosdbStockAllocateHistoryMapper;

    @Override
    public StockAllocateHistory create(StockAllocateHistory stockAllocateHistory) {
        CosmosdbStockAllocateHistory cosmosdbStockAllocateHistory= cosmosdbStockAllocateHistoryMapper
                    .stockAllocateHistoryToCosmosdbStockAllocateHistory(stockAllocateHistory);

        int sequence = getSequenceNumber(STOCK_ALLOCATE_HISTORY_TABLE_NAME);
        cosmosdbStockAllocateHistory.setId(sequence);

        try {
            persist(cosmosdbStockAllocateHistory);
        } catch (MongoWriteException e) {
            if (e.getCode() == 11000) {
                //E11000 duplicate key error collection:
                throw new DuplicateRequestException(e);
            } else {
                throw e;
            }
        }
        return stockAllocateHistory;
    }

    @Override
    public StockAllocateHistory updateStatus(StockAllocateHistory stockAllocateHistory) {
        int idx = 0;
        StringBuilder expressionIn = new StringBuilder();
        Parameters whereParameters = Parameters.with("requestId", stockAllocateHistory.getRequestId()).and("productId",
                stockAllocateHistory.getProductId());

        for (AllocateStatus status : stockAllocateHistory.getPreconditionStatusWhenChangeStatus()) {
            expressionIn = expressionIn.length() == 0 ? expressionIn.append(":status" + idx)
                    : expressionIn.append(", :status" + idx);
            whereParameters.and("status" + idx, status);
            idx++;
        }

        long result = update("{'Status': :status}", Parameters.with("status", stockAllocateHistory.getStatus()))
                .where("{'RequestId': :requestId,'ProductId': :productId, 'Status': { '$in': ["
                        + expressionIn.toString() + "] } }",
                        whereParameters);

        if (result == 0l) {
            throw new DuplicateRequestException("DuplicateRequest");
        }
        return stockAllocateHistory;
    }

    @Override
    public List<StockAllocateHistory> getStockAllocateHistoryByRequestId(String requestId) {
        return cosmosdbStockAllocateHistoryMapper
                .cosmosdbStockAllocateHistoryToStockAllocateHistory(find("RequestId", requestId).list());
    }

       /**
     * Mongodbのfunctionでシーケンスを発番する。
     * @param tableName テーブル名
     * @return シーケンス
     */
    private int getSequenceNumber(String tableName) {
        // 本来はマイクロサービスの管理単位を跨ぐような（データベースを跨ぐような）DBアクセスは禁止。
        // サービス毎にシーケンスCollectionを作成するとCosmosdbのコストが高くなる、また作業簡略化のためCommonDBへのアクセスを行う。
        // 時間ができたら共通サービスを作成して発番機能を作る。
        Document result = mongoClient.getDatabase("Common").getCollection("Sequence").findOneAndUpdate(
                Filters.eq("_id", tableName),
                new Document("$inc", new Document("SequenceNumber", 1)),
                new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
        return result.getInteger("SequenceNumber");
    }
}
