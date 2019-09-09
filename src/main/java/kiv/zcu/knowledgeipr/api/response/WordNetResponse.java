package kiv.zcu.knowledgeipr.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kiv.zcu.knowledgeipr.analysis.wordnet.AnalysisType;
import kiv.zcu.knowledgeipr.analysis.wordnet.AnalyzedWord;

import java.util.List;

public class WordNetResponse {

    private AnalysisType type;

    @JsonProperty("analysis")
    private List<AnalyzedWord> synonyms;

    public WordNetResponse(List<AnalyzedWord> words, AnalysisType type) {
        this.synonyms = words;
        this.type = type;
    }

    public List<AnalyzedWord> getSynonyms() {
        return synonyms;
    }

    public AnalysisType getType() {
        return type;
    }
}
