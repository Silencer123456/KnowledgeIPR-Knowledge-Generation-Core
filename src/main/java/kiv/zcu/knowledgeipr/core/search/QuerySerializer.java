package kiv.zcu.knowledgeipr.core.search;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class QuerySerializer extends StdSerializer<Query> {
    protected QuerySerializer(Class<Query> t) {
        super(t);
    }

    @Override
    public void serialize(Query query, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();


    }
}
