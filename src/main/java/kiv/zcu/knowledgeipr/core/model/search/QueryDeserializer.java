package kiv.zcu.knowledgeipr.core.model.search;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A handler for deserialization of the query body from JSON into the <code>Query</code> class instance
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

        // QueryOption map
        TypeReference<Map<String, Object>> optionsRef = new TypeReference<Map<String, Object>>() {
        };
        JsonNode optionsNode = node.get("options");
        Map<String, Object> options = optionsNode == null ? new HashMap<>() : mapper.readValue(mapper.treeAsTokens(optionsNode), optionsRef);


        JsonNode fieldsNode = node.get("fields");
        List<String> fields = fieldsNode == null ? new ArrayList<>() : mapper.readValue(mapper.writeValueAsString(fieldsNode), new TypeReference<List<String>>() {
        });

        Query q = new Query(filters, conditions, options);
        q.setFields(fields);

        return q;
    }
}
