package kiv.zcu.knowledgeipr.analysis.summarizer;

import java.util.ArrayList;
import java.util.List;

public class SentenceBuilder {
    // declaring local variables
    private static final String SEPERATORS = ". ";
    private static List<String> lines;
    private static ArrayList<Sentence> sentenceObjects = new ArrayList<Sentence>();


    public SentenceBuilder(String language, String filePath) {
        // build list of Sentence objects
        getSentences(filePath);
    }

    // getter
    public static ArrayList<Sentence> getSentenceObjects() {
        return sentenceObjects;
    }

    public static List<String> getLines() {
        return lines;
    }


    private ArrayList<Sentence> getSentences(String path) {

        String[] splitLines = path.split("(?<=\\. {0,1})");

        for (int j = 0; j < splitLines.length; j++) {
            if (splitLines[j].equals(" "))
                splitLines[j] = null;
        }

        //System.out.println(i + "st run");
        for (String aSentence : splitLines) {
            //System.out.println(sentence);
            if (aSentence != null) {
                Sentence s = new Sentence(aSentence);
                sentenceObjects.add(s);
            }
        }
        return sentenceObjects;
    }

}

