package kiv.zcu.knowledgeipr.api.filter;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Enables logging of incoming client HTTP requests.
 */
@Logged
@Provider
public class RequestLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Context
    private HttpServletRequest servletRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        UriInfo info = requestContext.getUriInfo();
        StringBuilder sb = new StringBuilder();
        sb.append("------------- NEW HTTP REQUEST FROM ").append(servletRequest.getRemoteAddr()).append(" --------------").append("\n");
        sb.append(" - User: ").append(requestContext.getSecurityContext().getUserPrincipal() == null ? "unknown"
                : requestContext.getSecurityContext().getUserPrincipal()).append("\n");
        sb.append(" - Path: ").append(info.getPath()).append("\n");
        sb.append(" - Query params: ").append(getQueryParamsString(info)).append("\n");
        sb.append(" - Header: ").append(requestContext.getHeaders()).append("\n");
        sb.append(" - Entity: ").append(getEntityBody(requestContext)).append("\n");
        sb.append("-----------------------------------------------------------------------------------------").append("\n");
        LOGGER.info(sb.toString());
    }

    private String getQueryParamsString(UriInfo info) {
        MultivaluedMap<String, String> queryParams = info.getQueryParameters();
        StringBuilder sb = new StringBuilder();
        for (String str : queryParams.keySet()) {
            sb.append(str).append(" -> ").append(queryParams.getFirst(str)).append("; ");
        }

        return sb.toString();
    }

    private String getEntityBody(ContainerRequestContext requestContext) {
        InputStream in = requestContext.getEntityStream();

        final StringBuilder b = new StringBuilder();
        try {
            byte[] requestEntity = IOUtils.toByteArray(in);
            if (requestEntity.length == 0) {
                b.append("\n");
            } else {
                b.append(new String(requestEntity)).append("\n");
            }
            requestContext.setEntityStream(new ByteArrayInputStream(requestEntity));

        } catch (IOException ex) {
            //Handle logging error
            ex.printStackTrace();
        }
        return b.toString();
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext responseContext) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("Header: ").append(responseContext.getHeaders()).append("\n");
        sb.append(" - Entity: ").append(responseContext.getEntity()).append("\n");
        LOGGER.finer("HTTP RESPONSE : " + sb.toString());
    }
}
