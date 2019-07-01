package kiv.zcu.knowledgeipr.analysis.summarizer;

import java.util.ArrayList;
import java.util.Collections;

public class Summarizer {
    private ArrayList<Word> maxWordList = WordBuilder.getMaxWordList();


    public Summarizer() {
    }


    public void sortTopNWordList() {
        Collections.sort(maxWordList, new WordComparator() {
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

        // print final summary
        for (int i = 0; i < maxWordList.size(); i++) {
            int j = maxWordList.get(i).getBelongingSentenceNo();
            summary.append(sentences.get(j));
        }
        return summary;
    }
}