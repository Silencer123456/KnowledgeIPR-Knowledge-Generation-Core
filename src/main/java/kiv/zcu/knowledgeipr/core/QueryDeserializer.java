package kiv.zcu.knowledgeipr.core;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.Map;

public class QueryDeserializer extends StdDeserializer<Query> {


    protected QueryDeserializer(Class<?> vc) {
        super(vc);
    }

    protected QueryDeserializer() {
        this(null);
    }

    @Override
    public Query deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String sourceType = node.get("sourceType").getTextValue();

        TypeReference<Map<String, String>> filterRef = new TypeReference<Map<String, String>>() {
        };
        Map<String, String> filters = mapper.readValue(node.get("filters"), filterRef);

        TypeReference<Map<String, Map<String, Integer>>> conditionsRef =
                new TypeReference<Map<String, Map<String, Integer>>>() {
                };
        Map<String, Map<String, Integer>> conditions = mapper.readValue(node.get("conditions"), conditionsRef);

        TypeReference<Map<String, Object>> optionsRef = new TypeReference<Map<String, Object>>() {
        };
        Map<String, Object> options = mapper.readValue(node.get("options"), optionsRef);

        return new Query(sourceType, filters, conditions, options);
    }
}
