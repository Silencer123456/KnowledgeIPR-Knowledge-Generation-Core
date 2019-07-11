package kiv.zcu.knowledgeipr.analysis.summarizer.lib;

import java.util.ArrayList;

public class Summarizer {
    private ArrayList<Word> maxWordList = WordBuilder.getMaxWordList();

    public Summarizer() {
    }

    public void sortTopNWordList() {
        maxWordList.sort(new WordComparator() {
            @Override
            public int compare(Word s1, Word s2) {
                return s1.getBelongingSentenceNo() > s2.getBelongingSentenceNo() ? 1 : -1;
            }

        });
    }

    /**
     * Create final summary.
     */
    public StringBuilder createSummary() {
        ArrayList<Sentence> sentences = SentenceBuilder.getSentenceObjects();
        StringBuilder summary = new StringBuilder();

        for (Word word : maxWordList) {
            int j = word.getBelongingSentenceNo();
            summary.append(sentences.get(j));
        }

        return summary;
    }
}