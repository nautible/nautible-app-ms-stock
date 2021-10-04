package jp.co.ogis_ri.nautible.app.stock.outbound.cosmosdb;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import jp.co.ogis_ri.nautible.app.stock.config.QuarkusMappingConfig;
import jp.co.ogis_ri.nautible.app.stock.domain.StockAllocateHistory;

/**
 * DynamodbのEntityとDomainのマッピング定義
 */
@Mapper(config = QuarkusMappingConfig.class)
public interface CosmosdbStockAllocateHistoryMapper {

    CosmosdbStockAllocateHistoryMapper INSTANCE = Mappers.getMapper(CosmosdbStockAllocateHistoryMapper.class);

    CosmosdbStockAllocateHistory stockAllocateHistoryToCosmosdbStockAllocateHistory(
            StockAllocateHistory stockAllocateHistory);

    StockAllocateHistory cosmosdbStockAllocateHistoryToStockAllocateHistory(
            CosmosdbStockAllocateHistory stockAllocateHistory);

    List<StockAllocateHistory> cosmosdbStockAllocateHistoryToStockAllocateHistory(
            List<CosmosdbStockAllocateHistory> stockAllocateHistory);

}
