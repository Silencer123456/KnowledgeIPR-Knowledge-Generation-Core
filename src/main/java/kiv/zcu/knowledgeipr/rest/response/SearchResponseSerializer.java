package kiv.zcu.knowledgeipr.rest.response;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class SearchResponseSerializer extends StdSerializer<SearchResponse> {

    public SearchResponseSerializer(Class<SearchResponse> t) {
        super(t);
    }

    public SearchResponseSerializer() {
        this(null);
    }

    @Override
    public void serialize(SearchResponse searchResponse, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(searchResponse.getMsg());
        jsonGenerator.writeObject(searchResponse.getStatus());

    }
}
