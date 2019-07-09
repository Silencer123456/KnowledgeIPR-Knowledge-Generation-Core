package kiv.zcu.knowledgeipr.core.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kiv.zcu.knowledgeipr.rest.exception.ResponseSerializationException;
import kiv.zcu.knowledgeipr.rest.response.Response;
import kiv.zcu.knowledgeipr.rest.response.StatusResponse;

public class SerializationUtils {

    public static String serializeObject(Object o) throws ResponseSerializationException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ResponseSerializationException(new Response(StatusResponse.ERROR, "Could not serialize the response"));
        }
    }
}
