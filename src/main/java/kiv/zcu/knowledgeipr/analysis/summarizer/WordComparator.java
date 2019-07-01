package kiv.zcu.knowledgeipr.analysis.summarizer;

import java.util.Comparator;

public class WordComparator implements Comparator<Word> {

    public int compare(Word w1, Word w2) {
        if (w1.getBelongingSentenceNo() < w2.getBelongingSentenceNo())
            return -1;
        else if (w1.getBelongingSentenceNo() > w2.getBelongingSentenceNo())
            return 1;

        return 0;

    }
}
