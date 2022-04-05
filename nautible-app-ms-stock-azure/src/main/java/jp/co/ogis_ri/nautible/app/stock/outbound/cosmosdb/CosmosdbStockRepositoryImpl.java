package jp.co.ogis_ri.nautible.app.stock.outbound.cosmosdb;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jp.co.ogis_ri.nautible.app.stock.domain.AllocateStatus;
import jp.co.ogis_ri.nautible.app.stock.domain.Stock;
import jp.co.ogis_ri.nautible.app.stock.domain.StockAllocateHistory;
import jp.co.ogis_ri.nautible.app.stock.domain.StockAllocateHistoryRepository;
import jp.co.ogis_ri.nautible.app.stock.domain.StockRepository;

/**
 * 在庫レポジトリ
 */
@Named("CosmosdbStockRepositoryImpl")
@ApplicationScoped
public class CosmosdbStockRepositoryImpl
        implements StockRepository, PanacheMongoRepositoryBase<CosmosdbStock, Integer> {
    /** 在庫テーブル名 */
    private static final String STOCK_TABLE_NAME = "Stock";
    @Inject
    MongoClient mongoClient;
    @Inject
    CosmosdbStockMapper cosmosdbStockMapper;
    @Inject
    StockAllocateHistoryRepository stockAllocateHistoryRepository;

    @Override
    public Stock getByStockId(int stockId) {
        return cosmosdbStockMapper.cosmosdbStockToStock((CosmosdbStock) find("_id", stockId).firstResult());
    }

    @Override
    public Stock getByProductId(int productId) {
        return cosmosdbStockMapper.cosmosdbStockToStock((CosmosdbStock) find("ProductId", productId).firstResult());
    }

    @Override
    public List<Stock> find() {
        return cosmosdbStockMapper.cosmosdbStockToStock(findAll(Sort.by("_id")).list());
    }

    @Override
    public Stock create(Stock stock) {
        int sequence = getSequenceNumber(STOCK_TABLE_NAME);
        stock.setId(sequence);
        persist(cosmosdbStockMapper.stockToCosmosdbStock(stock));
        return stock;
    }

    @Override
    public void delete(int stockId) {
        delete("_id", stockId);
    }

    @Override
    public Stock update(Stock stock) {
        update(cosmosdbStockMapper.stockToCosmosdbStock(stock));
        return stock;
    }

    @Override
    public StockAllocateResult allocate(StockAllocateHistory stockAllocateHistory) {

        // 履歴の作成更新はConditionを指定することで冪等性対応を実現しているので最初に履歴を作成する。
        stockAllocateHistory = stockAllocateHistory.getStatus() == AllocateStatus.PENDING
                ? stockAllocateHistoryRepository.create(stockAllocateHistory)
                : stockAllocateHistoryRepository.updateStatus(stockAllocateHistory);

        //int productId, int quantity, int reservedQuantity
        int quantity, reservedQuantity;
        switch (stockAllocateHistory.getStatus()) {
        case PENDING:
            quantity = stockAllocateHistory.getQuantity() * -1;
            reservedQuantity = stockAllocateHistory.getQuantity();
            break;
        case ALLOCATED:
            quantity = 0;
            reservedQuantity = stockAllocateHistory.getQuantity() * -1;
            break;
        default:
            // REJECT:
            quantity = stockAllocateHistory.getQuantity();
            reservedQuantity = stockAllocateHistory.getQuantity() * -1;
            break;
        }

        Stock stock = getByProductId(stockAllocateHistory.getProductId());
        long result = update("{ $inc: {Quantity: :quantity , ReservedQuantity: :reservedQuantity } } ",
                Parameters.with("quantity", quantity).and("reservedQuantity", reservedQuantity)).where(
                        "{ _id: :id , Quantity: { $gt: :reservedQuantity } }",
                        Parameters.with("id", stock.getId()).and("reservedQuantity", reservedQuantity));

        if (result == 0l) {
            // 在庫の残数以上の引当を行った場合
            stockAllocateHistory.reject();
            stockAllocateHistoryRepository.updateStatus(stockAllocateHistory);
            return new StockAllocateResult().stockAllocateHistory(stockAllocateHistory);

        }
        stock = getByProductId(stockAllocateHistory.getProductId());

        return new StockAllocateResult().stockAllocateHistory(stockAllocateHistory)
                .stockReservedQuantity(stock.getReservedQuantity())
                .stockQuantity(stock.getQuantity());
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
