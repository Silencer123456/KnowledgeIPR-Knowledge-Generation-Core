package kiv.zcu.knowledgeipr.analysis.summarizer;

import java.util.List;

public class TextSummarizer {
    private final String LANGCODE = "EN";

    public StringBuilder summarizeText(List<String> inputFiles) {
//        String summary = null;
        //Conceate List of strings into one string

        String joinedFiles = String.join(" ", inputFiles);

        SentenceBuilder sb = new SentenceBuilder(LANGCODE, joinedFiles);

        WordBuilder wb = new WordBuilder();
        wb.getWords(LANGCODE, joinedFiles);

        wb.removeStopWords(LANGCODE);
        wb.doCount(wb.getCleanWordObjects());
        wb.findTopNWords(5);


//        System.out.println(wb.getfreqMap());
        Summarizer summm = new Summarizer();
        summm.sortTopNWordList();

        return summm.createSummary();
    }
}
