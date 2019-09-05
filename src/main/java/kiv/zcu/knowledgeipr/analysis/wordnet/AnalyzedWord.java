package kiv.zcu.knowledgeipr.analysis.wordnet;

import java.util.List;

/**
 * Contains analysis of a single word. The analysis can contain synonyms or antonyms etc.
 * The information what exactly is stored is not present as it is now not important.
 */
public class AnalyzedWord {

    private String description;
    private List<String> words;

    public AnalyzedWord(String description, List<String> words) {
        this.description = description;
        this.words = words;
    }

    public List<String> getWords() {
        return words;
    }
}
