package kiv.zcu.knowledgeipr.core.query;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A handler for deserialization of JSON into the <code>Query</code> class instance
 */
public class QueryDeserializer extends StdDeserializer<Query> {


    private QueryDeserializer(Class<?> vc) {
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

        // Source type
        JsonNode sourceTypeNode = node.get("sourceType");
        String sourceType = sourceTypeNode == null ? "" : sourceTypeNode.getTextValue();

        // Filters map
        TypeReference<Map<String, String>> filterRef = new TypeReference<Map<String, String>>() {
        };
        JsonNode filtersNode = node.get("filters");
        Map<String, String> filters = filtersNode == null ? new HashMap<>() : mapper.readValue(filtersNode, filterRef);

        // Conditions map
        TypeReference<Map<String, Map<String, Integer>>> conditionsRef = new TypeReference<Map<String, Map<String, Integer>>>() {
        };
        JsonNode conditionsNode = node.get("conditions");
        Map<String, Map<String, Integer>> conditions = conditionsNode == null ? new HashMap<>()
                : mapper.readValue(conditionsNode, conditionsRef);

        // Options map
        TypeReference<Map<String, Object>> optionsRef = new TypeReference<Map<String, Object>>() {
        };
        JsonNode optionsNode = node.get("options");
        Map<String, Object> options = optionsNode == null ? new HashMap<>() : mapper.readValue(optionsNode, optionsRef);

        return new Query(sourceType, filters, conditions, options);
    }
}
