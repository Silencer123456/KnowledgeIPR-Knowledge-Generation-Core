package kiv.zcu.knowledgeipr.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kiv.zcu.knowledgeipr.rest.StatusResponse;
import kiv.zcu.knowledgeipr.rest.exception.ResponseSerializationException;
import kiv.zcu.knowledgeipr.rest.response.Response;

public class SerializationUtils {

    public static String serializeObject(Object o) throws ResponseSerializationException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new ResponseSerializationException(new Response(StatusResponse.ERROR, "Could not serialize the response"));
        }
    }
}
