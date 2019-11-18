package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.websearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class WikiSearcher {

    public static JsonNode searchWiki(final String query, final int limit) {
        try {
            String url = "https://en.wikipedia.org/w/api.php?action=opensearch&search=" + URLEncoder.encode(query, "UTF-8") + "&limit=" + limit + "&namespace=0&format=json";
            return readJsonFromUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return JsonNodeFactory.instance.objectNode();
    }

    private static JsonNode readJsonFromUrl(String url) throws IOException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);

            ObjectMapper mapper = new ObjectMapper();

            return mapper.readTree(jsonText);
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
