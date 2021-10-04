package jp.co.ogis_ri.nautible.app.stock.outbound.dynamodb;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import jp.co.ogis_ri.nautible.app.stock.config.QuarkusMappingConfig;
import jp.co.ogis_ri.nautible.app.stock.domain.StockAllocateHistory;

/**
 * DynamodbのEntityとDomainのマッピング定義
 */
@Mapper(config = QuarkusMappingConfig.class)
public interface DynamodbStockAllocateHistoryMapper {

    DynamodbStockAllocateHistoryMapper INSTANCE = Mappers.getMapper(DynamodbStockAllocateHistoryMapper.class);

    DynamodbStockAllocateHistory stockAllocateHistoryToDynamodbStockAllocateHistory(
            StockAllocateHistory stockAllocateHistory);

    StockAllocateHistory dynamodbStockAllocateHistoryToStockAllocateHistory(
            DynamodbStockAllocateHistory stockAllocateHistory);

    List<StockAllocateHistory> dynamodbStockAllocateHistoryToStockAllocateHistory(
            List<DynamodbStockAllocateHistory> stockAllocateHistory);

}
