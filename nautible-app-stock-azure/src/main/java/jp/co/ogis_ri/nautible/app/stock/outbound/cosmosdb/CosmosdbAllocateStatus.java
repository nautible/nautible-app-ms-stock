package jp.co.ogis_ri.nautible.app.stock.outbound.cosmosdb;

/**
 * 引当ステータス<br>
 * Semantic Lock カウンターメジャー<br>
 * <li>PENDING→ALLOCATED・・・引当成功時のスタータス遷移
 * <li>PENDING→REJECTED・・・在庫不足や他のサービスの処理結果が原因で、補償トランザクションで引当取り消し時のスタータス遷移
 */
public enum CosmosdbAllocateStatus {

    /** ペンディング */
    PENDING,
    /** 引当済み */
    ALLOCATED,
    /** 却下 */
    REJECTED

}
