package kiv.zcu.knowledgeipr.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class WordNetResponse {

    @JsonProperty("synonyms")
    private List<String> synonyms;

    @JsonProperty("hypernyms")
    private List<String> hypernyms;


    public WordNetResponse(List<String> synonyms, List<String> hypernyms) {
        this.synonyms = synonyms;
        this.hypernyms = hypernyms;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public List<String> getHypernyms() {
        return hypernyms;
    }
}
