package jp.co.ogis_ri.nautible.app.stock.domain;

import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.PublishEventRequestBuilder;
import jp.co.ogis_ri.nautible.app.order.client.rest.CreateOrderReply;
import jp.co.ogis_ri.nautible.app.order.client.rest.CreateOrderReply.ProcessTypeEnum;

/**
 * 在庫のSAGAマネージャ
 */
@ApplicationScoped
public class StockSagaManager {
    /** orderのpubsub名 */
    private static final String ORDER_PUBSUB_NAME = "order-pubsub";
    // デフォルトのapplication/jsonだとsubscriber側でCloudEventに変換するとデータ部がMapになる。
    // steamにしてObjectMapperでマッピングした方が扱いやすい。
    private static final String PUBSUB_CONTENT_TYPE = "application/octet-stream";

    /**
     * 引当予約の正常応答を返す
     * @param requestId リクエストID
     */
    public void replyReserveAllocate(String requestId) {
        replyAllocate(requestId, "create-order-reply",
                new CreateOrderReply().status(200).requestId(requestId)
                        .processType(ProcessTypeEnum.STOCK_RESERVE_ALLOCATE));
    }

    /**
     * 引当承認の正常応答を返す
     * @param requestId リクエストID
     */
    public void replyApproveAllocate(String requestId) {
        replyAllocate(requestId, "create-order-reply",
                new CreateOrderReply().status(200).requestId(requestId)
                        .processType(ProcessTypeEnum.STOCK_APPROVE_ALLOCATE));
    }

    /**
     * 引当却下の正常応答を返す
     * @param requestId リクエストID
     */
    public void rejectReserveAllocate(String requestId) {
        replyAllocate(requestId, "create-order-reply",
                new CreateOrderReply().status(200).requestId(requestId)
                        .processType(ProcessTypeEnum.STOCK_REJECT_ALLOCATE));
    }

    /**
     * 引当予約のリクエスト不正の応答を返す
     * @param requestId リクエストID
     * @param message メッセージ
     */
    public void replyReserveAllocateBadRequest(String requestId, String message) {
        replyAllocate(requestId, "create-order-reply",
                new CreateOrderReply().status(400).requestId(requestId)
                        .processType(ProcessTypeEnum.STOCK_RESERVE_ALLOCATE).message(message));
    }

    /**
     * 引当承認のリクエスト不正の応答を返す
     * @param requestId リクエストID
     */
    public void replyApproveAllocateBadRequest(String requestId, String message) {
        replyAllocate(requestId, "create-order-reply",
                new CreateOrderReply().status(400).requestId(requestId)
                        .processType(ProcessTypeEnum.STOCK_APPROVE_ALLOCATE).message(message));
    }

    /**
     * 引当却下のリクエスト不正の応答を返す
     * @param requestId リクエストID
     */
    public void replyRejectAllocateBadRequest(String requestId, String message) {
        replyAllocate(requestId, "create-order-reply",
                new CreateOrderReply().status(400).requestId(requestId)
                        .processType(ProcessTypeEnum.STOCK_REJECT_ALLOCATE).message(message));
    }

    /**
     * SAGA。引当応答。Daprのpubsubを利用して、応答のpublishを行う。
     * @param requestId リクエストId
     * @param replyTopic 応答のtopic
     * @param data 応答data
     */
    private void replyAllocate(String requestId, String replyTopic, Object data) {
        // WARNING 分散トレーシングのIstio/Daprが共存できない。個別には実現できる。
        // DaprはW3Cのspecを採用、IstioはW3CのSpecには現状未対応。
        // IstioがW3Cに対応したらうまく共存できるかも？https://github.com/istio/istio/issues/23960
        executeDaprClient(
                c -> c.publishEvent(new PublishEventRequestBuilder(ORDER_PUBSUB_NAME, replyTopic, data)
                        .withContentType(PUBSUB_CONTENT_TYPE).build())
                        .block());
    }

    private <R> R executeDaprClient(Function<DaprClient, R> func) {
        try (DaprClient client = new DaprClientBuilder().build()) {
            return func.apply(client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
