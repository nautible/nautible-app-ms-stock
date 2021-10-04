package jp.co.ogis_ri.nautible.app.stock.inbound.rest;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.dapr.client.domain.CloudEvent;
import jp.co.ogis_ri.nautible.app.stock.api.rest.DaprSubscribe;
import jp.co.ogis_ri.nautible.app.stock.api.rest.RestCreateStockRequest;
import jp.co.ogis_ri.nautible.app.stock.api.rest.RestStockService;
import jp.co.ogis_ri.nautible.app.stock.api.rest.RestUpdateStockRequest;
import jp.co.ogis_ri.nautible.app.stock.api.rest.StockApproveAllocateRequest;
import jp.co.ogis_ri.nautible.app.stock.api.rest.StockRejectAllocateRequest;
import jp.co.ogis_ri.nautible.app.stock.api.rest.StockReserveAllocateRequest;
import jp.co.ogis_ri.nautible.app.stock.core.rest.MDC;
import jp.co.ogis_ri.nautible.app.stock.domain.Stock;
import jp.co.ogis_ri.nautible.app.stock.domain.StockService;

/**
 * REST APIの在庫サービス。REST APIのエンドポイント。
 */
@MDC
public class RestStockServiceImpl implements RestStockService {

    /** stockのpubsub */
    private static final String STOCK_PUBSUB_NAME = "stock-pubsub";
    @Inject
    StockService service;
    @Inject
    RestStockMapper mapper;
    @Inject
    ObjectMapper objectMapper;
    @Inject
    Validator validator;

    Logger LOG = Logger.getLogger(RestStockServiceImpl.class.getName());

    @Override
    public Response getByStockId(@PathParam("stockId") Integer stockId) {
        Stock stock = service.getByStockId(stockId);
        return stock == null ? Response.status(Status.NOT_FOUND).build()
                : Response.ok(mapper.stockToRestGetByStockIdResponse(stock)).build();
    }

    @Override
    public Response getByProductId(@PathParam("productId") Integer productId) {
        Stock stock = service.getByProductId(productId);
        return stock == null ? Response.status(Status.NOT_FOUND).build()
                : Response.ok(mapper.stockToRestGetByProductIdResponse(stock)).build();
    }

    @Override
    public Response findAll() {
        List<Stock> stock = service.findAll();
        return (stock == null || stock.isEmpty()) ? Response.status(Status.NOT_FOUND).build()
                : Response.ok(mapper.stockToRestFindAllResponse(stock)).build();
    }

    @Override
    public Response create(@Valid RestCreateStockRequest stock) {
        Stock stockRet = service.create(mapper.restCreateStockRequestToStock(stock));
        return stockRet == null ? Response.status(Status.NOT_FOUND).build()
                : Response.ok(mapper.stockToRestCreateStockResponse(stockRet)).build();
    }

    @Override
    public Response update(@Valid RestUpdateStockRequest stock) {
        Stock stockRet = service.update(mapper.restUpdateStockRequestToStock(stock));
        return stockRet == null ? Response.status(Status.NOT_FOUND).build()
                : Response.ok(mapper.stockToRestUpdateStockResponse(stockRet)).build();
    }

    @Override
    public Response delete(@PathParam("stockId") Integer stockId) {
        service.deleteByStockId(stockId);
        return Response.status(Status.NO_CONTENT).build();
    }

    @Override
    public Response daprSubscribe() {
        // https://docs.dapr.io/developing-applications/building-blocks/pubsub/howto-publish-subscribe/#programmatic-subscriptions
        return Response.ok(List.of(
                new DaprSubscribe().pubsubname(STOCK_PUBSUB_NAME).topic("stock-reserve-allocate")
                        .route("/stock/reserveAllocate/"),
                new DaprSubscribe().pubsubname(STOCK_PUBSUB_NAME).topic("stock-approve-allocate")
                        .route("/stock/approveAllocate/"),
                new DaprSubscribe().pubsubname(STOCK_PUBSUB_NAME).topic("stock-reject-allocate")
                        .route("/stock/rejectAllocate/")))
                .build();
    }

    @Override
    public Response reserveAllocate(@Valid byte[] body) {
        final StockReserveAllocateRequest request = readCloudEventRequest(body, StockReserveAllocateRequest.class);
        Set<ConstraintViolation<StockReserveAllocateRequest>> violations = validator.validate(request);
        if (violations.isEmpty() == false) {
            service.replyReserveAllocateBadRequest(request.getRequestId(), "Bad request.");
            return Response.ok().build();
        }
        service.reserveAllocate(request.getRequestId(), request.getProducts().stream()
                .map(p -> Pair.of(p.getProductId(), p.getQuantity())).collect(Collectors.toList()));

        return Response.ok().build();
    }

    @Override
    public Response approveAllocate(@Valid byte[] body) {
        final StockApproveAllocateRequest request = readCloudEventRequest(body, StockApproveAllocateRequest.class);
        Set<ConstraintViolation<StockApproveAllocateRequest>> violations = validator.validate(request);
        if (violations.isEmpty() == false) {
            service.replyReserveAllocateBadRequest(request.getRequestId(), "Bad request.");
            return Response.ok().build();
        }
        service.approveAllocate(request.getRequestId());

        return Response.ok().build();
    }

    @Override
    public Response rejectAllocate(@Valid byte[] body) {
        final StockRejectAllocateRequest request = readCloudEventRequest(body, StockRejectAllocateRequest.class);
        Set<ConstraintViolation<StockRejectAllocateRequest>> violations = validator.validate(request);
        if (violations.isEmpty() == false) {
            service.replyRejectAllocateBadRequest(request.getRequestId(), "Bad request.");
            return Response.ok().build();
        }
        service.rejectAllocate(request.getRequestId());

        return Response.ok().build();
    }

    private <R> R readCloudEventRequest(byte[] body, Class<R> clazz) {
        try {
            CloudEvent event = CloudEvent.deserialize(body);
            return objectMapper.readValue(event.getBinaryData(), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
