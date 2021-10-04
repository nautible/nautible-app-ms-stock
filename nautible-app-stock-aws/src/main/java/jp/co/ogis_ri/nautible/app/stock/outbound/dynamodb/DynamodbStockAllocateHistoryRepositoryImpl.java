package jp.co.ogis_ri.nautible.app.stock.outbound.dynamodb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jp.co.ogis_ri.nautible.app.stock.domain.AllocateStatus;
import jp.co.ogis_ri.nautible.app.stock.domain.StockAllocateHistory;
import jp.co.ogis_ri.nautible.app.stock.domain.StockAllocateHistoryRepository;
import jp.co.ogis_ri.nautible.app.stock.exception.DuplicateRequestException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

/**
 * 在庫レポジトリ
 */
@Named("DynamodbStockAllocateHistoryRepositoryImpl")
@ApplicationScoped
public class DynamodbStockAllocateHistoryRepositoryImpl implements StockAllocateHistoryRepository {
    /** 在庫引当テーブル名 */
    private static final String STOCK_ALLOCATE_HISTORY_TABLE_NAME = "StockAllocateHistory";
    @Inject
    DynamoDbClient dynamoDB;
    @Inject
    DynamodbStockAllocateHistoryMapper dynamodbStockAllocateHistoryMapper;

    @Override
    public StockAllocateHistory create(StockAllocateHistory stockAllocateHistory) {
        // 冪等性対応
        PutItemEnhancedRequest<DynamodbStockAllocateHistory> request = PutItemEnhancedRequest
                .builder(DynamodbStockAllocateHistory.class)
                .item(dynamodbStockAllocateHistoryMapper
                        .stockAllocateHistoryToDynamodbStockAllocateHistory(stockAllocateHistory))
                .conditionExpression(Expression.builder().expression("attribute_not_exists(RequestId)").build())
                .build();
        try {
            getTable("StockAllocateHistory", DynamodbStockAllocateHistory.class)
                    .putItem(request);
        } catch (ConditionalCheckFailedException e) {
            throw new DuplicateRequestException(e);
        }
        return stockAllocateHistory;
    }

    @Override
    public List<StockAllocateHistory> getStockAllocateHistoryByRequestId(String requestId) {
        Key key = Key.builder().partitionValue(requestId).build();
        QueryConditional queryConditional = QueryConditional.keyEqualTo(key);
        List<DynamodbStockAllocateHistory> result = getTable(STOCK_ALLOCATE_HISTORY_TABLE_NAME,
                DynamodbStockAllocateHistory.class)
                        .query(r -> r.queryConditional(queryConditional)).items().stream().collect(Collectors.toList());
        return dynamodbStockAllocateHistoryMapper.dynamodbStockAllocateHistoryToStockAllocateHistory(result);
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

    @Override
    public StockAllocateHistory updateStatus(StockAllocateHistory stockAllocateHistory) {
        int idx = 0;
        StringBuilder expressionInValue = new StringBuilder();
        Map<String, AttributeValue> expressionValues = new HashMap<String, AttributeValue>();
        for (AllocateStatus status : stockAllocateHistory.getPreconditionStatusWhenChangeStatus()) {
            expressionInValue = expressionInValue.length() == 0 ? expressionInValue.append(":status" + idx)
                    : expressionInValue.append(", :status" + idx);
            expressionValues.put(":status" + idx, AttributeValue.builder().s(String.valueOf(status)).build());
            idx++;
        }

        // 冪等性対応
        PutItemEnhancedRequest<DynamodbStockAllocateHistory> request = PutItemEnhancedRequest
                .builder(DynamodbStockAllocateHistory.class)
                .item(dynamodbStockAllocateHistoryMapper
                        .stockAllocateHistoryToDynamodbStockAllocateHistory(stockAllocateHistory))
                .conditionExpression(Expression.builder().expression("#s IN ( " + expressionInValue.toString() + " )")
                        .putExpressionName("#s", "Status")
                        .expressionValues(expressionValues)
                        .build())
                .build();

        try {
            getTable("StockAllocateHistory", DynamodbStockAllocateHistory.class)
                    .putItem(request);
        } catch (ConditionalCheckFailedException e) {
            throw new DuplicateRequestException(e);
        }
        return stockAllocateHistory;
    }

}
