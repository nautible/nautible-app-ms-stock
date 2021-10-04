package jp.co.ogis_ri.nautible.app.stock.inbound.grpc;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.grpc.stub.StreamObserver;
import jp.co.ogis_ri.nautible.app.stock.api.grpc.Empty;
import jp.co.ogis_ri.nautible.app.stock.api.grpc.GrpcCreateStockRequest;
import jp.co.ogis_ri.nautible.app.stock.api.grpc.GrpcCreateStockResponse;
import jp.co.ogis_ri.nautible.app.stock.api.grpc.GrpcDeleteByStockIdRequest;
import jp.co.ogis_ri.nautible.app.stock.api.grpc.GrpcFindAllStockResponse;
import jp.co.ogis_ri.nautible.app.stock.api.grpc.GrpcGetByProductIdRequest;
import jp.co.ogis_ri.nautible.app.stock.api.grpc.GrpcGetByProductIdResponse;
import jp.co.ogis_ri.nautible.app.stock.api.grpc.GrpcGetByStockIdRequest;
import jp.co.ogis_ri.nautible.app.stock.api.grpc.GrpcGetByStockIdResponse;
import jp.co.ogis_ri.nautible.app.stock.api.grpc.GrpcUpdateStockRequest;
import jp.co.ogis_ri.nautible.app.stock.api.grpc.GrpcUpdateStockResponse;
import jp.co.ogis_ri.nautible.app.stock.api.grpc.StockServiceGrpc;
import jp.co.ogis_ri.nautible.app.stock.domain.Stock;
import jp.co.ogis_ri.nautible.app.stock.domain.StockService;

/**
 * gRPCの在庫サービス。gRPCのエンドポイント。
 */
@Singleton
public class GrpcStockService extends StockServiceGrpc.StockServiceImplBase {

    @Inject
    StockService service;
    @Inject
    GrpcStockMapper mapper;

    Logger LOG = Logger.getLogger(GrpcStockService.class.getName());

    @Override
    public void getByStockId(GrpcGetByStockIdRequest request,
            StreamObserver<GrpcGetByStockIdResponse> responseObserver) {
        int stockId = request.getStockId();
        Stock stock = service.getByStockId(stockId);
        if (stock == null) {
            responseObserver.onNext(GrpcGetByStockIdResponse.newBuilder().build());
        } else {
            responseObserver.onNext(GrpcGetByStockIdResponse.newBuilder()
                    .setStock(mapper.stockToGrpcStock(stock)).build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getByProductId(GrpcGetByProductIdRequest request,
            StreamObserver<GrpcGetByProductIdResponse> responseObserver) {
        int productId = request.getProductId();
        Stock stock = service.getByProductId(productId);
        if (stock == null) {
            responseObserver.onNext(GrpcGetByProductIdResponse.newBuilder().build());
        } else {
            responseObserver.onNext(GrpcGetByProductIdResponse.newBuilder()
                    .setStock(mapper.stockToGrpcStock(stock)).build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void deleteByStockId(GrpcDeleteByStockIdRequest request, StreamObserver<Empty> responseObserver) {
        service.deleteByStockId(request.getStockId());
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void create(GrpcCreateStockRequest request, StreamObserver<GrpcCreateStockResponse> responseObserver) {
        Stock stock = service.create(mapper.grpcStockToStock(request.getStock()));
        responseObserver
                .onNext(GrpcCreateStockResponse.newBuilder().setStock(mapper.stockToGrpcStock(stock)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void update(GrpcUpdateStockRequest request, StreamObserver<GrpcUpdateStockResponse> responseObserver) {
        Stock stock = service.update(mapper.grpcStockToStock(request.getStock()));
        responseObserver
                .onNext(GrpcUpdateStockResponse.newBuilder().setStock(mapper.stockToGrpcStock(stock)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void findAll(Empty request, StreamObserver<GrpcFindAllStockResponse> responseObserver) {
        List<Stock> stock = service.findAll();
        responseObserver.onNext(
                GrpcFindAllStockResponse.newBuilder().addAllStocks(mapper.stockToGrpcStock(stock)).build());
        responseObserver.onCompleted();
    }

}
