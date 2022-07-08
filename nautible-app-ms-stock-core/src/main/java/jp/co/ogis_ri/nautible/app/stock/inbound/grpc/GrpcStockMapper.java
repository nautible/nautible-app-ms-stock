package jp.co.ogis_ri.nautible.app.stock.inbound.grpc;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import jp.co.ogis_ri.nautible.app.stock.api.grpc.GrpcStock;
import jp.co.ogis_ri.nautible.app.stock.config.QuarkusMappingConfig;
import jp.co.ogis_ri.nautible.app.stock.domain.Stock;

/**
 * gRPCのRequset/ReplyオブジェクトとDomainのマッピング定義
 */
@Mapper(config = QuarkusMappingConfig.class)
public interface GrpcStockMapper {

    GrpcStockMapper INSTANCE = Mappers.getMapper(GrpcStockMapper.class);

    @Mapping(target = "id", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "productId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "quantity", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "reservedQuantity", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    GrpcStock stockToGrpcStock(Stock from, @MappingTarget GrpcStock.Builder to);

    default GrpcStock stockToGrpcStock(Stock from) {
        return stockToGrpcStock(from, GrpcStock.newBuilder());
    }

    List<GrpcStock> stockToGrpcStock(List<Stock> stock);

    Stock grpcStockToStock(GrpcStock stock);

    List<Stock> grpcStockToStock(List<GrpcStock> stock);

}
