package kiv.zcu.knowledgeipr.core.query;

import kiv.zcu.knowledgeipr.core.utils.SerializationUtils;
import kiv.zcu.knowledgeipr.rest.errorhandling.ObjectSerializationException;

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
