package kiv.zcu.knowledgeipr.utils;

import java.util.Collections;
import java.util.List;

public class Common {
    /**
     * Returns a modified list of objects with elements from the specified range, indicated by page and limit parameters
     *
     * @param list  - The original list from which to extract a new list in range
     * @param page  - The starting number of the range
     * @param limit - The size of the range
     * @return List constructed from the original references list containing only the elements from the calculated range
     */
    public static <T> List<T> getListFromRange(List<T> list, int page, int limit) {
        int beginIndex = (page - 1) * limit;
        int endIndex = beginIndex + limit;
        if (list.size() < beginIndex) {
            return Collections.emptyList();
        }

        return list.subList(beginIndex, Math.min(list.size(), endIndex));
    }
}
