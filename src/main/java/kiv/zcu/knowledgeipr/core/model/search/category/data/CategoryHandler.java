package kiv.zcu.knowledgeipr.core.model.search.category.data;

import kiv.zcu.knowledgeipr.api.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.app.AppServletContextListener;
import kiv.zcu.knowledgeipr.core.model.search.category.tree.TreeNode;
import kiv.zcu.knowledgeipr.utils.AppConstants;
import kiv.zcu.knowledgeipr.utils.SerializationUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class CategoryHandler {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private TreeNode<Category> root;

    public CategoryHandler() {
        try {
            Properties properties = AppServletContextListener.getProperties();
            String basePath = properties.getProperty(AppConstants.CATEGORY_JSON_RESOURCE);
            readCategories(new JsonCategoryTreeReader(Paths.get(basePath)));
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reads the categories from a specified category reader
     *
     * @param reader - Reader implementation responsible for reading the category tree
     * @return Root node representing the category tree
     */
    public void readCategories(ICategoryTreeReader reader) {
        try {
            root = reader.read();
            LOGGER.info("Categories read successfully");
        } catch (CategoryReadException e) {
            e.printStackTrace();
            LOGGER.warning(e.getMessage());
        }
    }

    public boolean containsCategory(String category) {
        Comparable<Category> searchCriteria = treeData -> {
            boolean nodeOk = treeData.getName().equalsIgnoreCase(category);
            return nodeOk ? 0 : 1;
        };

        TreeNode<Category> found = root.findTreeNode(searchCriteria);

        return found != null;
    }

    public TreeNode<Category> getCategory(String category) {
        Comparable<Category> searchCriteria = treeData -> {
            boolean nodeOk = treeData.getName().equalsIgnoreCase(category);
            return nodeOk ? 0 : 1;
        };

        TreeNode<Category> found = root.findTreeNode(searchCriteria);

        return found;
    }

    public List<String> getNodesAtLevel(int level) {
        List<String> nodes = new ArrayList<>();

        for (TreeNode<Category> node : root) {
            //String indent = createIndent(node.getLevel());
            //System.out.println(indent + node.data);
            if (node.getLevel() == level) {
                nodes.add(node.data.getName());
            }
        }

        return nodes;
    }

    public TreeNode<Category> getRoot() {
        return root;
    }

    public String getTreeAsJson(String categoryName) throws ObjectSerializationException {
        if (categoryName == null) {
            return SerializationUtils.serializeObject(root);
        } else {
            return SerializationUtils.serializeObject(getCategory(categoryName));
        }
    }

    public String getSubtreeAsString(String categoryName) {
        if (categoryName == null) {
            return getSubtreeAsString(root);
        } else {
            return getSubtreeAsString(getCategory(categoryName));
        }
    }

    private String getSubtreeAsString(TreeNode<Category> topNode) {
        StringBuilder sb = new StringBuilder();
        if (topNode == null) {
            return "";
        }
        for (TreeNode<Category> node : topNode) {
            String indent = createIndent(node.getLevel());
            sb.append(indent).append(node.data.getName()).append("\n");
        }

        return sb.toString();
    }

    private String createIndent(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("\t");
            if (i == depth - 1) {
                sb.append("-");
            }
        }
        return sb.toString();
    }
}
