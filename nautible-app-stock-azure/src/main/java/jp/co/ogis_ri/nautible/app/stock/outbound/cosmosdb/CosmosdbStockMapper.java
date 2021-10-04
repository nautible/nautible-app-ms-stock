package jp.co.ogis_ri.nautible.app.stock.outbound.cosmosdb;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import jp.co.ogis_ri.nautible.app.stock.config.QuarkusMappingConfig;
import jp.co.ogis_ri.nautible.app.stock.domain.Stock;

/**
 * DynamodbのEntityとDomainのマッピング定義
 */
@Mapper(config = QuarkusMappingConfig.class)
public interface CosmosdbStockMapper {

    CosmosdbStockMapper INSTANCE = Mappers.getMapper(CosmosdbStockMapper.class);

    @Mapping(target = "id", expression = "java(stock.getProductId())")
    CosmosdbStock stockToCosmosdbStock(Stock stock);

    Stock cosmosdbStockToStock(CosmosdbStock stock);

    List<Stock> cosmosdbStockToStock(List<CosmosdbStock> stock);

}
