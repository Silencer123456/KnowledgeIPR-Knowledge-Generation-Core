package kiv.zcu.knowledgeipr.core.model.search.category.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import kiv.zcu.knowledgeipr.core.model.search.category.tree.TreeNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

/**
 * Reads the categories from JSON format. It can be read from string or from
 * the filesystem.
 */
public class JsonCategoryTreeReader implements ICategoryTreeReader {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static final ObjectMapper objectMapper = new ObjectMapper();


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
        try {
            JsonNode json = objectMapper.readTree(this.json);

            Category category = new Category(json.get("name").textValue(), extractKeywords(json));

            TreeNode<Category> treeRoot = new TreeNode<>(category);

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
    private void readTree(JsonNode jsonNode, TreeNode<Category> treeNode) throws IOException {
        JsonNode children = jsonNode.get("children"); // Must be an array
        for (JsonNode child : children) {
            String categoryName = child.get("name").textValue();
            TreeNode<Category> childNode = treeNode.addChild(new Category(categoryName, extractKeywords(child)));
            readTree(child, childNode);
        }
    }

    private List<String> extractKeywords(JsonNode json) throws IOException {
        return objectMapper.readValue(json.get("keywords").toString(), TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
    }
}
