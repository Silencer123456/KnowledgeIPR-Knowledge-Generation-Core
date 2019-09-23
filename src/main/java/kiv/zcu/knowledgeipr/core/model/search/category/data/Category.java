package kiv.zcu.knowledgeipr.core.model.search.category.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Category {
    /**
     * The name of the category
     */
    private String name;

    /**
     * The list of keywords associated with this category
     */
    @JsonIgnore
    private List<String> keywords;

    public Category(String name, List<String> keywords) {
        this.name = name;
        this.keywords = keywords;
        if (!keywords.contains(name)) {
            this.keywords.add(name);
        }
    }

    public Category(String name) {
        this(name, new ArrayList<>());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Category)) return false;

        Category other = (Category) obj;

        return name.equals(other.name) && keywords.equals(other.keywords);
    }

    @Override
    public int hashCode() {
        int result = 17;
        if (name != null) {
            result = 31 * result + name.hashCode();
        }
        if (keywords != null) {
            result = 31 * result + keywords.hashCode();
        }

        return result;
    }

    public void addKeyword(String keyword) {
        keywords.add(keyword);
    }

    public String getName() {
        return name;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * Returns a list of keywords as a string separated by a delimiter
     *
     * @param delimiter separator of the individual elements
     * @return String with a list of delimiter separated keywords
     */
    public String getKeywordsSeparatedBy(String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keywords.size(); i++) {
            String keyword = keywords.get(i);
            if (i == keywords.size() - 1) {
                sb.append(keyword);
            } else {
                sb.append(keyword).append(delimiter);
            }
        }

        return sb.toString();
    }
}
