package kiv.zcu.knowledgeipr.core.model.search;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;

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
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        // Source type
        JsonNode sourceTypeNode = node.get("sourceType");
        String sourceTypeText = sourceTypeNode == null ? "" : sourceTypeNode.textValue();
        DataSourceType sourceType;
        try {
            sourceType = DataSourceType.valueOf(sourceTypeText);
        } catch (IllegalArgumentException e) {
            sourceType = DataSourceType.PATENT; // TODO: Handle better
        }

        // Filters map
        TypeReference<Map<String, String>> filterRef = new TypeReference<Map<String, String>>() {
        };
        JsonNode filtersNode = node.get("filters");
        Map<String, String> filters = filtersNode == null ? new HashMap<>() : mapper.readValue(mapper.treeAsTokens(filtersNode), filterRef);

        // Conditions map
        TypeReference<Map<String, Map<String, Integer>>> conditionsRef = new TypeReference<Map<String, Map<String, Integer>>>() {
        };
        JsonNode conditionsNode = node.get("conditions");
        Map<String, Map<String, Integer>> conditions = conditionsNode == null ? new HashMap<>()
                : mapper.readValue(mapper.treeAsTokens(conditionsNode), conditionsRef);

        // Options map
        TypeReference<Map<String, Object>> optionsRef = new TypeReference<Map<String, Object>>() {
        };
        JsonNode optionsNode = node.get("options");
        Map<String, Object> options = optionsNode == null ? new HashMap<>() : mapper.readValue(mapper.treeAsTokens(optionsNode), optionsRef);

        return new Query(filters, conditions, options);
    }
}
