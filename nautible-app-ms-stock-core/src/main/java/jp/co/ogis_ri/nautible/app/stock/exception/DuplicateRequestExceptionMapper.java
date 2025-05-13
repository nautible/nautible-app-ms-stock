package jp.co.ogis_ri.nautible.app.stock.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * REST API用の例外ハンドラ
 */
@Provider
public class DuplicateRequestExceptionMapper implements ExceptionMapper<DuplicateRequestException> {

    Logger LOG = Logger.getLogger(DuplicateRequestExceptionMapper.class.getName());

    @Override
    public Response toResponse(DuplicateRequestException e) {
        LOG.log(Level.INFO, "Duplicate request.", e);
        // 冪等性対応。重複処理要求なので200応答を返却して終了する。
        return Response.ok().build();
    }

}
