package kiv.zcu.knowledgeipr.core.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kiv.zcu.knowledgeipr.rest.errorhandling.ObjectSerializationException;

import java.io.IOException;
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
            throw new ObjectSerializationException("Could not serialize the object");
        }
    }

    /**
     * Returns a JSON tree from an object by first serializing it to string and then reading it as tree
     *
     * @param o - object to parse as JSON tree
     * @return - JSON node
     * @throws ObjectSerializationException
     */
    public static JsonNode getTreeFromObject(Object o) throws ObjectSerializationException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            String json = mapper.writeValueAsString(o);
            return mapper.readTree(json);

        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
            e.printStackTrace();
            throw new ObjectSerializationException("Could not create JSON tree from object");
        }
    }
}
