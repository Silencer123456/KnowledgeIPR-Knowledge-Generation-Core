package kiv.zcu.knowledgeipr.analysis.summarizer.lib;

import java.util.*;

public class WordBuilder {
    private static ArrayList<Word> dirtyWordObjects = new ArrayList<Word>();                        // Word-objects with stop-words
    private static ArrayList<Word> cleanWordObjects;                                                // Word-objects without stop-words
    private static LinkedHashMap<Word, Integer> freqMap = new LinkedHashMap<Word, Integer>();        // Word:Frequency-map 			why use linkedhashmap?   order = insertion-order
    private static ArrayList<Word> maxWordList = new ArrayList<Word>();                                    // Top N words based on occurrence

    public WordBuilder() {
    }

    // getter
    public static ArrayList<Word> getCleanWordObjects() {
        return cleanWordObjects;
    }

    // getter
    public static ArrayList<Word> getDirtyWordObjects() {
        return dirtyWordObjects;
    }

    // getter
    public static LinkedHashMap<Word, Integer> getfreqMap() {
        return freqMap;
    }

    public static ArrayList<Word> getMaxWordList() {
        return maxWordList;
    }

    public List<Word> getWords(String language, String path) {
        // get list of sentence objects from SentenceBuilder
        //SentenceBuilder sb = new SentenceBuilder();
        ArrayList<Sentence> sentenceObjects = SentenceBuilder.getSentenceObjects();

        String[] wordsForCurrentSentence = null;

        // loop through every sentence
        for (Sentence sentence : sentenceObjects) {
            if (language.equals("NO"))
                wordsForCurrentSentence = sentence.getText().split("([^\\wæøåÆØÅ]+)");    // norwegian: split for every non-word (including æøå)
            else if (language.equals("EN"))
                wordsForCurrentSentence = sentence.getText().split("([^\\w']+)");        // 	english:  split for every non-word (including ')
            else
                System.err.println("Please set a valid language code.");

            // sentence number
            int sentenceNo = sentence.getSentenceNo();

            // for every sentence, add every word
            for (String word : wordsForCurrentSentence) {
                Word w = new Word(word.toLowerCase(), sentenceNo);
                dirtyWordObjects.add(w);
            }

        }
        return dirtyWordObjects;
    }

    public ArrayList<Word> removeStopWords(String language) {
        List<String> stopWords = null;

        stopWords = Arrays.asList("a", "able", "about", "above", "abst", "accordance", "according", "accordingly", "across", "act", "actually", "added", "adj", "affected", "affecting", "affects", "after", "afterwards", "again", "against", "ah", "all", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "announce", "another", "any", "anybody", "anyhow", "anymore", "anyone", "anything", "anyway", "anyways", "anywhere", "apparently", "approximately", "are", "aren", "arent", "arise", "around", "as", "aside", "ask", "asking", "at", "auth", "available", "away", "awfully", "b", "back", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "begin", "beginning", "beginnings", "begins", "behind", "being", "believe", "below", "beside", "besides", "between", "beyond", "biol", "both", "brief", "briefly", "but", "by", "c", "ca", "came", "can", "cannot", "can't", "cause", "causes", "certain", "certainly", "co", "com", "come", "comes", "contain", "containing", "contains", "could", "couldnt", "d", "date", "did", "didn't", "different", "do", "does", "doesn't", "doing", "done", "don't", "down", "downwards", "due", "during", "e", "each", "ed", "edu", "effect", "eg", "eight", "eighty", "either", "else", "elsewhere", "end", "ending", "enough", "especially", "et", "et-al", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "except", "f", "far", "few", "ff", "fifth", "first", "five", "fix", "followed", "following", "follows", "for", "former", "formerly", "forth", "found", "four", "from", "further", "furthermore", "g", "gave", "get", "gets", "getting", "give", "given", "gives", "giving", "go", "goes", "gone", "got", "gotten", "h", "had", "happens", "hardly", "has", "hasn't", "have", "haven't", "having", "he", "hed", "hence", "her", "here", "hereafter", "hereby", "herein", "heres", "hereupon", "hers", "herself", "hes", "hi", "hid", "him", "himself", "his", "hither", "home", "how", "howbeit", "however", "hundred", "i", "id", "ie", "if", "i'll", "i'm", "immediate", "immediately", "importance", "important", "in", "inc", "indeed", "index", "information", "instead", "into", "invention", "inward", "is", "isn't", "it", "it'd", "it'll", "its", "itself", "i've", "j", "just", "k", "keep", "keeps", "kept", "kg", "km", "know", "known", "knows", "l", "largely", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "line", "little", "'ll", "look", "looking", "looks", "ltd", "m", "made", "mainly", "make", "makes", "many", "may", "maybe", "me", "mean", "means", "meantime", "meanwhile", "merely", "mg", "might", "million", "miss", "ml", "more", "moreover", "most", "mostly", "mr", "mrs", "much", "mug", "must", "my", "myself", "n", "na", "name", "namely", "nay", "nd", "near", "nearly", "necessarily", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "ninety", "no", "nobody", "non", "none", "nonetheless", "noone", "nor", "normally", "nos", "not", "noted", "nothing", "now", "nowhere", "o", "obtain", "obtained", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "omitted", "on", "once", "one", "ones", "only", "onto", "or", "ord", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "owing", "own", "p", "page", "pages", "part", "particular", "particularly", "past", "per", "perhaps", "placed", "please", "plus", "poorly", "possible", "possibly", "potentially", "pp", "predominantly", "present", "previously", "primarily", "probably", "promptly", "proud", "provides", "put", "q", "que", "quickly", "quite", "qv", "r", "ran", "rather", "rd", "re", "readily", "really", "recent", "recently", "ref", "refs", "regarding", "regardless", "regards", "related", "relatively", "research", "respectively", "resulted", "resulting", "results", "right", "runAggregation", "s", "said", "same", "saw", "say", "saying", "says", "sec", "section", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sent", "seven", "several", "shall", "she", "shed", "she'll", "shes", "should", "shouldn't", "show", "showed", "shown", "showns", "shows", "significant", "significantly", "similar", "similarly", "since", "six", "slightly", "so", "some", "somebody", "somehow", "someone", "somethan", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specifically", "specified", "specify", "specifying", "still", "stop", "strongly", "sub", "substantially", "successfully", "such", "sufficiently", "suggest", "sup", "sure", "t", "take", "taken", "taking", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "that'll", "thats", "that've", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "thered", "therefore", "therein", "there'll", "thereof", "therere", "theres", "thereto", "thereupon", "there've", "these", "they", "theyd", "they'll", "theyre", "they've", "think", "this", "those", "thou", "though", "thoughh", "thousand", "throug", "through", "throughout", "thru", "thus", "til", "tip", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "ts", "twice", "two", "u", "un", "under", "unfortunately", "unless", "unlike", "unlikely", "until", "unto", "up", "upon", "ups", "us", "use", "used", "useful", "usefully", "usefulness", "uses", "using", "usually", "v", "value", "various", "'ve", "very", "via", "viz", "vol", "vols", "vs", "w", "want", "wants", "was", "wasnt", "way", "we", "wed", "welcome", "we'll", "went", "were", "werent", "we've", "what", "whatever", "what'll", "whats", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "wheres", "whereupon", "wherever", "whether", "which", "while", "whim", "whither", "who", "whod", "whoever", "whole", "who'll", "whom", "whomever", "whos", "whose", "why", "widely", "willing", "wish", "with", "within", "without", "wont", "words", "world", "would", "wouldnt", "www", "x", "y", "yes", "yet", "you", "youd", "you'll", "your", "youre", "yours", "yourself", "yourselves", "you've", "z", "zero");

        cleanWordObjects = new ArrayList<>(Word.getAllDirtyWords());                // copy array
        for (Word word : dirtyWordObjects) {
            if (stopWords.contains(word.getWordText())) {
                // remove stop-words
                cleanWordObjects.remove(word);
            }

        }
        return cleanWordObjects;
    }

    // count occurrence of each word
    public LinkedHashMap<Word, Integer> doCount(ArrayList<Word> list) {
        int freq;
        Word currentWord;

        for (int i = 0; i < list.size(); i++) {
            currentWord = list.get(i);

            // only runAggregation code IFF the key doesn't already exist in the freqMap
            if (!freqMap.containsKey(currentWord)) {
                // check frequency of the given string
                freq = Collections.frequency(list, currentWord);
                freqMap.put(currentWord, freq);    // (word, frequency)

                currentWord.setWordOccurence(freq);
            }
        }
        return freqMap;
    }

    public void findTopNWords(int nTopEntries /* boolean nonConsecutive*/) {
        if (nTopEntries == 0) {
            System.err.println("Can't be 0.");
            return;
        } else if (nTopEntries > freqMap.size()) {
            System.err.println("Entry number higher than number of total entries.");
            System.err.println("Aborting...");
            return;
        }

        Word tempWord = null;
        List<Word> keys = new ArrayList<>(freqMap.keySet());

        int counter = 0;
        while (counter != nTopEntries) {
            Word maxWord = null;

            for (Word word : keys) {
                if (tempWord == null || word.compareTo(tempWord) > 0) {
                    tempWord = word;
                }
            }
            maxWord = tempWord;

            if (!checkSameSentenceNo(maxWordList, maxWord)) {
                maxWordList.add(maxWord);
                counter++;
            }

            keys.remove(maxWord);
            tempWord = null;
            maxWord = null;
        }
    }

    private boolean checkSameSentenceNo(ArrayList<Word> list, Word w) {
        if (w == null)
            return false;

        for (Word word : list) {
            if (word.getBelongingSentenceNo() == w.getBelongingSentenceNo())
                return true;
        }

        return false;
    }

}






