package jp.co.ogis_ri.nautible.app.stock.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import jp.co.ogis_ri.nautible.app.stock.domain.StockRepository;

/**
 * ヘルスチェック。 Dynamodbへの接続を検証する.{@link HealthCheck}。
 */
@Readiness
@ApplicationScoped
public class StockHealthCheck implements HealthCheck {

    @Inject
    Instance<StockRepository> stockRepository;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse.named("Dynamodb connection");

        try {
            stockRepository.get().getByStockId(Integer.MIN_VALUE);
            return builder.up().build();
        } catch (Throwable e) {
            return builder.down().build();
        }
    }

}
