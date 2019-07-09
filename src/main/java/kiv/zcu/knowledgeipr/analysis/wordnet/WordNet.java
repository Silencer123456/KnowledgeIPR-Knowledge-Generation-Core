package kiv.zcu.knowledgeipr.analysis.wordnet;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.*;
import net.sf.extjwnl.data.list.PointerTargetNode;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.dictionary.Dictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Enables access to WordNet lexical database
 */
public class WordNet {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private Dictionary dictionary = null;

    public WordNet() {
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
     * Returns a list of hypernyms words to the specified word in the parameter
     *
     * @param word - Word for which to find synonyms
     * @return lis of synonym words
     */
    public List<String> getHypernymsForWord(String word) {
        List<String> hypernymsList = new ArrayList<>();
        try {
            IndexWord indexWord = dictionary.getIndexWord(POS.NOUN, word);

            if (indexWord == null) return hypernymsList;

            List<Synset> set = indexWord.getSenses();

            if (set.isEmpty()) return hypernymsList;

            PointerTargetNodeList hypernyms = PointerUtils.getDirectHypernyms(set.get(0));
            for (PointerTargetNode node : hypernyms) {
                List<Word> words = node.getSynset().getWords();
                for (Word w : words) {
                    hypernymsList.add(w.getLemma());
                }
            }
        } catch (JWNLException e) {
            e.printStackTrace();
        }

        return hypernymsList;
    }

    /**
     * Gets synonym words for the specified word
     *
     * @param word - Word for which to find synonyms
     * @return
     */
    public List<String> getSynonymsForWord(String word) {
        List<String> synonyms = new ArrayList<>();
        try {
            IndexWord indexWord = dictionary.getIndexWord(POS.NOUN, word);

            if (indexWord == null) return synonyms;

            List<Synset> set = indexWord.getSenses();

            if (set.isEmpty()) return synonyms;

            List<Word> words = set.get(0).getWords();
            for (Word w : words) {
                synonyms.add(w.getLemma());
            }
        } catch (JWNLException e) {
            e.printStackTrace();
        }

        return synonyms;
    }
}
