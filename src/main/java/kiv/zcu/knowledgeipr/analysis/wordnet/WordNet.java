package kiv.zcu.knowledgeipr.analysis.wordnet;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.*;
import net.sf.extjwnl.data.list.PointerTargetNode;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.dictionary.Dictionary;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Enables access to WordNet lexical database
 */
public class WordNet {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static WordNet instance;

    private Dictionary dictionary = null;

    private WordNet() {
        init();
    }

    private void init() {
        try {
            dictionary = Dictionary.getDefaultResourceInstance();
        } catch (JWNLException e) {
            LOGGER.warning("Could not load WordNet dictionary: " + e.getMessage());
            e.printStackTrace();
        }

        if (null != dictionary) {
            LOGGER.info("WordNet dictionary loaded");
        }
    }

    /**
     * Gets synonym words for the specified word
     *
     * @param word - Word for which to find synonyms
     * @return
     */
    public List<AnalyzedWord> getSynonymsForWord(String word) {
        List<AnalyzedWord> synonyms = new ArrayList<>();
        try {
            List<Synset> setList = getWordSenses(word);
            if (setList.isEmpty()) return synonyms;

            for (Synset set : setList) {
                List<String> tmp = new ArrayList<>();
                List<Word> words = set.getWords();
                for (Word w : words) {
                    tmp.add(w.getLemma());
                }

                synonyms.add(new AnalyzedWord(set.getGloss(), tmp));
            }

        } catch (JWNLException e) {
            e.printStackTrace();
        }

        return synonyms;
    }

    /**
     * Returns a list of hypernyms words to the specified word in the parameter
     *
     * @param word - Word for which to find synonyms
     * @return lis of synonym words
     */
    public List<AnalyzedWord> getAntonymsForWord(String word) {
        List<AnalyzedWord> hypernymsList = new ArrayList<>();
        try {
            List<Synset> setList = getWordSenses(word);
            if (setList.isEmpty()) {
                return hypernymsList;
            }

            for (Synset set : setList) {
                PointerTargetNodeList antonyms = PointerUtils.getAntonyms(set);
                hypernymsList.add(new AnalyzedWord(set.getGloss(), getWords(antonyms)));
            }

        } catch (JWNLException e) {
            e.printStackTrace();
        }

        return hypernymsList;
    }

    /**
     * Returns a list of hypernyms words to the specified word in the parameter
     *
     * @param word - Word for which to find synonyms
     * @return lis of synonym words
     */
    public List<AnalyzedWord> getHypernymsForWord(String word) {
        List<AnalyzedWord> hypernymsList = new ArrayList<>();
        try {
            List<Synset> setList = getWordSenses(word);
            if (setList.isEmpty()) {
                return hypernymsList;
            }

            for (Synset set : setList) {
                PointerTargetNodeList hypernyms = PointerUtils.getDirectHypernyms(set);
                hypernymsList.add(new AnalyzedWord(set.getGloss(), getWords(hypernyms)));
            }

        } catch (JWNLException e) {
            e.printStackTrace();
        }

        return hypernymsList;
    }

    /**
     * Returns a list of hyponyms words to the specified word in the parameter
     *
     * @param word - Word for which to find synonyms
     * @return lis of synonym words
     */
    public List<AnalyzedWord> getHyponymsForWord(String word) {
        List<AnalyzedWord> hypernymsList = new ArrayList<>();
        try {
            List<Synset> setList = getWordSenses(word);
            if (setList.isEmpty()) {
                return hypernymsList;
            }

            for (Synset set : setList) {
                PointerTargetNodeList hyponyms = PointerUtils.getDirectHyponyms(set);
                hypernymsList.add(new AnalyzedWord(set.getGloss(), getWords(hyponyms)));
            }

        } catch (JWNLException e) {
            e.printStackTrace();
        }

        return hypernymsList;
    }

    private List<String> getWords(PointerTargetNodeList list) {
        List<String> finalList = new ArrayList<>();

        for (PointerTargetNode node : list) {
            List<Word> words = node.getSynset().getWords();
            for (Word w : words) {
                finalList.add(w.getLemma());
            }
        }

        return finalList;
    }

    private List<Synset> getWordSenses(String word) throws JWNLException {
        IndexWord indexWord = dictionary.getIndexWord(POS.NOUN, word);
        if (indexWord == null) {
            indexWord = dictionary.getIndexWord(POS.VERB, word);
            if (indexWord == null) {
                indexWord = dictionary.getIndexWord(POS.ADJECTIVE, word);
                if (indexWord == null) {
                    indexWord = dictionary.getIndexWord(POS.ADVERB, word);
                    if (indexWord == null) {
                        return Collections.emptyList();
                    }
                }
            }
        }

        return indexWord.getSenses();
    }

    public static WordNet getInstance() {
        if (instance == null) {
            instance = new WordNet();
        }
        return instance;
    }

    public String getSynonymsForWordString(String word) {
        List<AnalyzedWord> list = getSynonymsForWord(word);
        if (list.isEmpty()) return word;

        List<String> syn = getSynonymsForWord(word).get(0).getWords();
        return StringUtils.join(syn, " ");
    }
}
