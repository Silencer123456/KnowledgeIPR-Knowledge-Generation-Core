package kiv.zcu.knowledgeipr.analysis.wordnet;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.dictionary.Dictionary;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Enables access to WordNet lexical database
 */
public class WordNet {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


    public void init() {
        Dictionary dictionary = null;

        try {
            dictionary = Dictionary.getDefaultResourceInstance();

        } catch (JWNLException e) {
            LOGGER.info("Could not load WordNet dictionary: " + e.getMessage());

            e.printStackTrace();
        }

        if (null != dictionary) {
            LOGGER.info("WordNet dictionary loaded");
            //(dictionary).go();
        }

//        if (HELP_KEYS.contains(args[0])) {
//            System.out.println(USAGE);
//        } else {
//            FileInputStream inputStream = new FileInputStream(args[0]);
//            dictionary = Dictionary.getInstance(inputStream);
//        }
//        }
    }

    /**
     * Returns a list of synonym words to the specified word in the parameter
     *
     * @param word - Word for which to find synonyms
     * @return lis of synonym words
     */
    public List<String> getSynonymsForWord(String word) {
        return Collections.emptyList();
    }
}
