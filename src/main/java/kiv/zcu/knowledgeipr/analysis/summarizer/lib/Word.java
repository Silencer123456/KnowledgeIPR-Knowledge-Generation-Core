package kiv.zcu.knowledgeipr.analysis.summarizer.lib;

import java.util.ArrayList;

public class Word {
    static ArrayList<Word> allDirtyWords = new ArrayList<Word>();
    private String wordText;
    private int belongsToSentenceN;
    private int occurrence = -1;

    public Word(String word, int belongsToSentenceN) {
        this.wordText = word;
        this.belongsToSentenceN = belongsToSentenceN;
        allDirtyWords.add(this);
    }

    public static ArrayList<Word> getAllDirtyWords() {
        return allDirtyWords;
    }

    @Override
    public String toString() {
//        return String.format("%-20s ...belongs to sentence %s", this.wordText, this.belongsToSentenceN);
        return String.format(this.wordText);
    }

    public String getWordText() {
        return this.wordText;
    }

    public int getBelongingSentenceNo() {
        return this.belongsToSentenceN;
    }

    public int getWordOccurence() {
        return this.occurrence;
    }

    public void setWordOccurence(int occurence) {
        this.occurrence = occurence;
    }

    @Override
    public boolean equals(Object obj) {
        //null instanceof Object will always return false
        if (!(obj instanceof Word))
            return false;

        //NOTE: '==' doesn't to the comparing properly. We have to use 'equals'.
        return this.wordText.equals(((Word) obj).wordText);
    }


    @Override
    public int hashCode() {
        return this.wordText.hashCode();    // we're basing the hashCode on 'wordText'
    }

    public int compareTo(Word word) {
        if (this.occurrence < word.occurrence)
            return -1;
        else if (this.occurrence > word.occurrence)
            return 1;

        return 0;

    }

}



