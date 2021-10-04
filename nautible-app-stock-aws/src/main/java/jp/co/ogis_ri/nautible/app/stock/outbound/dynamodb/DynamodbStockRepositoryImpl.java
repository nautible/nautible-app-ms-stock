package jp.co.ogis_ri.nautible.app.stock.outbound.dynamodb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jp.co.ogis_ri.nautible.app.stock.domain.AllocateStatus;
import jp.co.ogis_ri.nautible.app.stock.domain.Stock;
import jp.co.ogis_ri.nautible.app.stock.domain.StockAllocateHistory;
import jp.co.ogis_ri.nautible.app.stock.domain.StockAllocateHistoryRepository;
import jp.co.ogis_ri.nautible.app.stock.domain.StockRepository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest.Builder;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;

/**
 * 在庫レポジトリ
 */
@Named("DynamodbStockRepositoryImpl")
@ApplicationScoped
public class DynamodbStockRepositoryImpl implements StockRepository {
    /** 在庫テーブル名 */
    private static final String STOCK_TABLE_NAME = "Stock";
    @Inject
    DynamoDbClient dynamoDB;
    @Inject
    DynamodbStockMapper dynamodbStockMapper;
    @Inject
    StockAllocateHistoryRepository stockAllocateHistoryRepository;

    @Override
    public Stock getByStockId(int stockId) {
        Key key = Key.builder().partitionValue(stockId).build();
        DynamodbStock result = getTable(STOCK_TABLE_NAME, DynamodbStock.class).getItem(r -> r.key(key));
        return dynamodbStockMapper.dynamodbStockToStock(result);
    }

    @Override
    public Stock getByProductId(int productId) {
        // full scan
        return dynamodbStockMapper
                .dynamodbStockToStock(getTable(STOCK_TABLE_NAME, DynamodbStock.class).scan().items().stream()
                        .filter(s -> s.getProductId() == productId).findFirst()
                        .orElse(null));
    }

    @Override
    public List<Stock> find() {
        // full scan
        return dynamodbStockMapper.dynamodbStockToStock(
                getTable(STOCK_TABLE_NAME, DynamodbStock.class).scan().items().stream().collect(Collectors.toList()));
    }

    @Override
    public Stock create(Stock stock) {
        int sequence = getSequenceNumber(STOCK_TABLE_NAME);
        stock.setId(sequence);
        getTable(STOCK_TABLE_NAME, DynamodbStock.class).putItem(dynamodbStockMapper.stockToDynamodbStock(stock));
        return stock;
    }

    @Override
    public void delete(int stockId) {
        Key key = Key.builder().partitionValue(stockId).build();
        getTable(STOCK_TABLE_NAME, DynamodbStock.class).deleteItem(key);
    }

    @Override
    public Stock update(Stock stock) {
        getTable(STOCK_TABLE_NAME, DynamodbStock.class).updateItem(dynamodbStockMapper.stockToDynamodbStock(stock));
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

        // create order
        Stock stock = getByProductId(stockAllocateHistory.getProductId());
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("Id", AttributeValue.builder().n(String.valueOf(stock.getId())).build());
        Builder builder = UpdateItemRequest.builder().tableName(STOCK_TABLE_NAME).key(key)
                .updateExpression(
                        "SET Quantity = Quantity + :quantity, ReservedQuantity = ReservedQuantity + :reservedQuantity");
        // Conditionで残量以上の引当ができないようにすることで、マルチスレッド下で値が不正にならないようにする
        builder.expressionAttributeValues(//
                Map.of(":quantity", AttributeValue.builder().n(String.valueOf(quantity)).build(), //
                        ":reservedQuantity", AttributeValue.builder().n(String.valueOf(reservedQuantity)).build(), //
                        ":limit", AttributeValue.builder().n(String.valueOf(quantity * -1)).build()))
                .conditionExpression("Quantity >= :limit");
        UpdateItemRequest updateRequest = builder.returnValues(ReturnValue.UPDATED_NEW).build();
        UpdateItemResponse updateResponse = null;
        try {
            updateResponse = dynamoDB.updateItem(updateRequest);
        } catch (ConditionalCheckFailedException e) {
            // 在庫の残数以上の引当を行った場合
            stockAllocateHistory.reject();
            stockAllocateHistoryRepository.updateStatus(stockAllocateHistory);
            return new StockAllocateResult().stockAllocateHistory(stockAllocateHistory);
        }

        return new StockAllocateResult().stockAllocateHistory(stockAllocateHistory)
                .stockReservedQuantity(Integer.parseInt(updateResponse.attributes().get("ReservedQuantity").n()))
                .stockQuantity(Integer.parseInt(updateResponse.attributes().get("Quantity").n()));
    }

    /**
     * DynamoDbのアトミックカウンタを利用してシーケンスを発番する
     * @param tableName テーブル名
     * @return シーケンス
     */
    private int getSequenceNumber(String tableName) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("Name", AttributeValue.builder().s(tableName).build());
        Map<String, AttributeValueUpdate> update = new HashMap<>();
        update.put("SequenceNumber", AttributeValueUpdate.builder().value(AttributeValue.builder().n("1").build())
                .action(AttributeAction.ADD).build());
        UpdateItemRequest updateRequest = UpdateItemRequest.builder().tableName("Sequence").key(key)
                .attributeUpdates(update).returnValues(ReturnValue.UPDATED_NEW).build();
        UpdateItemResponse updateResponse = dynamoDB.updateItem(updateRequest);
        return Integer.parseInt(updateResponse.attributes().get("SequenceNumber").n());
    }

    /**
     * DynamoDbTableを取得する
     * @param <E> Mappingする型
     * @param tableName テーブル名
     * @param type Mappingする型
     * @return {@link DynamoDbTable}
     */
    private <E> DynamoDbTable<E> getTable(String tableName, Class<E> type) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDB).build();
        DynamoDbTable<E> mappedTable = enhancedClient.table(tableName, TableSchema.fromBean(type));
        return mappedTable;
    }

}
