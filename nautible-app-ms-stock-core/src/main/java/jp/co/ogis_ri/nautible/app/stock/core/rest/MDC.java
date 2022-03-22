package jp.co.ogis_ri.nautible.app.stock.core.rest;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

/**
 * {@link MDCInterceptor}の処理対象表すアノテーション
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@InterceptorBinding
public @interface MDC {

}
