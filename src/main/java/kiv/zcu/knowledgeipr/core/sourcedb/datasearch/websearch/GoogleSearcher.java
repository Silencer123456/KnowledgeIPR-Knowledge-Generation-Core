package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.websearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

public class GoogleSearcher {

    public static JsonNode search(final String query) {
        try {
            String apiKey = "AIzaSyCGSx8CoEOXT7BeYlc4S18peRng6twEAJs";
            String cx = "016400888427988646158:u5s9qasds9z";
            String url = "https://www.googleapis.com/customsearch/v1?q=" + URLEncoder.encode(query, "UTF8") + "&key=" + apiKey + "&cx=" + cx + "&searchType=image&alt=json";
            return readJsonFromUrl(url);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static JsonNode readJsonFromUrl(String url) throws IOException, GeneralSecurityException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = WebSearcher.readAll(rd);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(jsonText);

            return node;
        }
    }
}
