package kiv.zcu.knowledgeipr.core.model.search;

import kiv.zcu.knowledgeipr.api.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.utils.SerializationUtils;

public class QueryComparator {

    public static boolean compare(int firstHash, int secondHash) {
        try {
            if (SerializationUtils.serializeObject(firstHash).equals(SerializationUtils.serializeObject(secondHash))) {
                return true;
            }
        } catch (ObjectSerializationException e) {
            e.printStackTrace();
        }
        return false;
    }
}
