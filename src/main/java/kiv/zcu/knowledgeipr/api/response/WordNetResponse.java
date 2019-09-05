package kiv.zcu.knowledgeipr.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kiv.zcu.knowledgeipr.analysis.wordnet.AnalyzedWord;

import java.util.List;

public class WordNetResponse {

    @JsonProperty("analysis")
    private List<AnalyzedWord> synonyms;

    public WordNetResponse(List<AnalyzedWord> words) {
        this.synonyms = words;
    }

    public List<AnalyzedWord> getSynonyms() {
        return synonyms;
    }
}
