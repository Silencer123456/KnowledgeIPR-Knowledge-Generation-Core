package kiv.zcu.knowledgeipr.analysis.summarizer.lib;

public class Sentence {
    private static int nextN = 0;
    private String text;
    private int sentenceNo;


    public Sentence(String text) {
        this.text = text;
        this.sentenceNo = nextN++;
    }

    @Override
    public String toString() {
        return this.text;
    }

    public String getText() {
        return this.text;
    }

    public int getSentenceNo() {
        return this.sentenceNo;
    }

}
