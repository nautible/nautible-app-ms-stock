package jp.co.ogis_ri.nautible.app.stock.outbound.dynamodb;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import jp.co.ogis_ri.nautible.app.stock.config.QuarkusMappingConfig;
import jp.co.ogis_ri.nautible.app.stock.domain.Stock;

/**
 * DynamodbのEntityとDomainのマッピング定義
 */
@Mapper(config = QuarkusMappingConfig.class)
public interface DynamodbStockMapper {

    DynamodbStockMapper INSTANCE = Mappers.getMapper(DynamodbStockMapper.class);

    DynamodbStock stockToDynamodbStock(Stock stock);

    Stock dynamodbStockToStock(DynamodbStock stock);

    List<Stock> dynamodbStockToStock(List<DynamodbStock> stock);
}
