package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.websearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.Reader;

public class WebSearcher {

    /**
     * Executes the search of the web searching for the specified query
     *
     * @param query - The query string to search for on the web
     * @return - The results gathered from web in JSON format
     */
    public static JsonNode getWebSearchResults(final String query) {
        ObjectNode resultJson = new ObjectMapper().createObjectNode();

        JsonNode wikiJson = WikiSearcher.searchWiki(query, 5);
        resultJson.set("wikipedia", wikiJson);

        JsonNode googleJson = GoogleSearcher.search(query);
        resultJson.set("google", googleJson);

        return resultJson;
    }

    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
