package kiv.zcu.knowledgeipr.api.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
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

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        LOGGER.finer("---- New client request ----");
        LOGGER.finer(containerRequestContext.getMethod());
        UriInfo info = containerRequestContext.getUriInfo();
        if (info != null) {
            LOGGER.finer(info.getPath());
        }
        MediaType mediaType = containerRequestContext.getMediaType();
        if (mediaType != null) {
            LOGGER.finer(containerRequestContext.getMediaType().getType());
        }
        LOGGER.finer("----------------------------");
    }
}
