package kiv.zcu.knowledgeipr.core.search.category.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kiv.zcu.knowledgeipr.core.search.category.tree.TreeNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Reads the categories from JSON format. It can be read from string or from
 * the filesystem.
 */
public class JsonCategoryTreeReader implements ICategoryTreeReader {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


    private String json;

    /**
     * Constructor enables loading the category tree from String.
     *
     * @param json - JSON String containing the category tree
     */
    public JsonCategoryTreeReader(String json) {
        this.json = json;
    }

    /**
     * Constructor enables reading JSON from the filesystem
     * @param path - Path to the JSON file containing the category tree
     * @throws IOException
     */
    public JsonCategoryTreeReader(Path path) throws IOException {
        LOGGER.info("Attempting to read category tree from: " + path.toString());
        json = new String(Files.readAllBytes(path));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeNode<Category> read() throws CategoryReadException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode json = objectMapper.readTree(this.json);
            TreeNode<Category> treeRoot = new TreeNode<>(new Category(json.get("name").textValue()));

            readTree(json, treeRoot);

            return treeRoot;

        } catch (IOException e) {
            e.printStackTrace();
            throw new CategoryReadException("Error parsing category tree: " + e.getMessage());
        }
    }

    /**
     * Recursive function processing the json tree.
     *
     * @param jsonNode - Json node to process
     * @return - TreeNode instance
     */
    private void readTree(JsonNode jsonNode, TreeNode<Category> treeNode) {
        JsonNode children = jsonNode.get("children"); // Must be an array
        for (JsonNode child : children) {
            String categoryName = child.get("name").textValue();
            TreeNode<Category> childNode = treeNode.addChild(new Category(categoryName));
            readTree(child, childNode);
        }
    }
}
