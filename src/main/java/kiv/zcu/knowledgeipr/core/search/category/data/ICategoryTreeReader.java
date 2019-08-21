package kiv.zcu.knowledgeipr.core.search.category.data;

import kiv.zcu.knowledgeipr.core.search.category.tree.TreeNode;

/**
 * Interface enabling reading of categories from different sources.
 */
public interface ICategoryTreeReader {

    /**
     * Reads the categories from some source and returns a TreeNode representing the root
     * node of the tree.
     *
     * @return - Root node of the read tree
     * @throws CategoryReadException if the category tree cannot be read
     */
    TreeNode<Category> read() throws CategoryReadException;
}
