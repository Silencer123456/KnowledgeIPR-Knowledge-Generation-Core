package kiv.zcu.knowledgeipr.api.filter;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

/**
 * Enables logging of incoming client HTTP requests.
 */
@Logged
@Provider
public class RequestLoggingFilter implements ContainerRequestFilter {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Context
    private HttpServletRequest servletRequest;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        LOGGER.info("^^^^^^^^^^^^^^ New client request from " + servletRequest.getRemoteAddr() + " --------------");
        LOGGER.info(containerRequestContext.getMethod());
        UriInfo info = containerRequestContext.getUriInfo();
        if (info != null) {
            LOGGER.info(info.getPath());
        }
        MediaType mediaType = containerRequestContext.getMediaType();
        if (mediaType != null) {
            LOGGER.info(containerRequestContext.getMediaType().getType());
        }
        LOGGER.info("---------------------------------------");
    }
}
