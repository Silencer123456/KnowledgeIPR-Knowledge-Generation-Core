package kiv.zcu.knowledgeipr.core.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kiv.zcu.knowledgeipr.rest.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.rest.response.BaseResponse;
import kiv.zcu.knowledgeipr.rest.response.StatusResponse;

import java.util.logging.Logger;

public class SerializationUtils {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static String serializeObject(Object o) throws ObjectSerializationException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            LOGGER.warning(e.getMessage());
            e.printStackTrace();
            throw new ObjectSerializationException(new BaseResponse(StatusResponse.ERROR, "Could not serialize the object"));
        }
    }
}
