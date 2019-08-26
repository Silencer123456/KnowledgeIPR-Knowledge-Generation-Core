package kiv.zcu.knowledgeipr.analysis.summarizer;

import kiv.zcu.knowledgeipr.analysis.summarizer.lib.SentenceBuilder;
import kiv.zcu.knowledgeipr.analysis.summarizer.lib.Summarizer;
import kiv.zcu.knowledgeipr.analysis.summarizer.lib.WordBuilder;
import kiv.zcu.knowledgeipr.core.dataaccess.mongo.MongoRecord;

import java.util.ArrayList;
import java.util.List;

public class TextSummarizer {
    private final String LANGCODE = "EN";

    public StringBuilder summarizeTextMongo(List<MongoRecord> records) {
        List<String> input = new ArrayList<>();
        for (MongoRecord record : records) {
            input.add((String) record.getDocument().get("abstract"));
        }

        return summarizeText(input);
    }

    public StringBuilder summarizeText(List<String> inputFiles) {
//        String summary = null;
        //Conceate List of strings into one string

        String joinedFiles = String.join(" ", inputFiles);

        SentenceBuilder sb = new SentenceBuilder(LANGCODE, joinedFiles);

        WordBuilder wb = new WordBuilder();
        wb.getWords(LANGCODE, joinedFiles);

        wb.removeStopWords(LANGCODE);
        wb.doCount(WordBuilder.getCleanWordObjects());
        wb.findTopNWords(5);

//        System.out.println(wb.getfreqMap());
        Summarizer summm = new Summarizer();
        summm.sortTopNWordList();

        return summm.createSummary();
    }
}
