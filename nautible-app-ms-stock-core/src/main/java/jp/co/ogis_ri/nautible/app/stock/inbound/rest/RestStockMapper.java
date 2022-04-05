
package jp.co.ogis_ri.nautible.app.stock.inbound.rest;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import jp.co.ogis_ri.nautible.app.stock.api.rest.RestCreateStockRequest;
import jp.co.ogis_ri.nautible.app.stock.api.rest.RestCreateStockResponse;
import jp.co.ogis_ri.nautible.app.stock.api.rest.RestFindAllResponse;
import jp.co.ogis_ri.nautible.app.stock.api.rest.RestGetByProductIdResponse;
import jp.co.ogis_ri.nautible.app.stock.api.rest.RestGetByStockIdResponse;
import jp.co.ogis_ri.nautible.app.stock.api.rest.RestStock;
import jp.co.ogis_ri.nautible.app.stock.api.rest.RestUpdateStockRequest;
import jp.co.ogis_ri.nautible.app.stock.api.rest.RestUpdateStockResponse;
import jp.co.ogis_ri.nautible.app.stock.config.QuarkusMappingConfig;
import jp.co.ogis_ri.nautible.app.stock.domain.Stock;

/**
 * REST APIのRequset/ResponseオブジェクトとDomainのマッピング定義
 */
@Mapper(config = QuarkusMappingConfig.class)
public interface RestStockMapper {

    RestStockMapper INSTANCE = Mappers.getMapper(RestStockMapper.class);

    RestGetByStockIdResponse stockToRestGetByStockIdResponse(Stock stock);

    RestGetByProductIdResponse stockToRestGetByProductIdResponse(Stock stock);

    RestStock stockToRestStock(Stock stock);

    List<RestStock> stockToRestStock(List<Stock> stock);

    Stock restStockToStock(RestStock stock);

    List<Stock> restStockToStock(List<RestStock> stock);

    Stock restUpdateStockRequestToStock(RestUpdateStockRequest stock);

    Stock restCreateStockRequestToStock(RestCreateStockRequest stock);

    default RestFindAllResponse stockToRestFindAllResponse(List<Stock> stocks) {
        return new RestFindAllResponse().stocks(stockToRestStock(stocks));
    }

    default RestCreateStockResponse stockToRestCreateStockResponse(Stock stock) {
        return new RestCreateStockResponse().stock(stockToRestStock(stock));
    }

    default RestUpdateStockResponse stockToRestUpdateStockResponse(Stock stock) {
        return new RestUpdateStockResponse().stock(stockToRestStock(stock));
    }

}
