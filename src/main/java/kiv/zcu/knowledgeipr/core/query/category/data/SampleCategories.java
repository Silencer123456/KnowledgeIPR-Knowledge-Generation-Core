package kiv.zcu.knowledgeipr.core.query.category.data;

import kiv.zcu.knowledgeipr.core.query.category.tree.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class SampleCategories {

    private TreeNode<Category> root;

    public SampleCategories() {
        createCategories();
    }

    // TODO: Load from external source
    public TreeNode<Category> createCategories() {
        root = new TreeNode<>(new Category("Categories"));
        {
            TreeNode<Category> node0 = root.addChild(new Category("Vehicles"));
            {
                TreeNode<Category> node00 = node0.addChild(new Category("Wheels", new ArrayList<String>() {{
                    add("rims");
                    add("discs");
                    add("hubs");
                    add("axles");
                }}));
                TreeNode<Category> node01 = node0.addChild(new Category("Tyres"));
                TreeNode<Category> node02 = node0.addChild(new Category("Suspension"));
                TreeNode<Category> node03 = node0.addChild(new Category("Windows"));
                TreeNode<Category> node04 = node0.addChild(new Category("Brakes"));
                TreeNode<Category> node05 = node0.addChild(new Category("Land vehicles"));
            }
        }

        return root;
    }

    public boolean containsCategory(String category) {
        Comparable<Category> searchCriteria = treeData -> {
            if (treeData == null)
                return 1;
            boolean nodeOk = treeData.getName().equals(category);
            return nodeOk ? 0 : 1;
        };

        TreeNode<Category> found = root.findTreeNode(searchCriteria);

        return found != null;
    }

    public TreeNode<Category> getCategory(String category) {
        Comparable<Category> searchCriteria = treeData -> {
            if (treeData == null)
                return 1;
            boolean nodeOk = treeData.getName().equals(category);
            return nodeOk ? 0 : 1;
        };

        TreeNode<Category> found = root.findTreeNode(searchCriteria);

        return found;
    }

    public List<String> getNodesAtLevel(int level) {
        List<String> nodes = new ArrayList<>();

        TreeNode<Category> treeRoot = createCategories();
        for (TreeNode<Category> node : treeRoot) {
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

    public String getTreeAsString() {
        return getSubtreeAsString(root);
    }

    public String getSubtreeAsString(String categoryName) {
        return getSubtreeAsString(getCategory(categoryName));
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
            sb.append(' ');
        }
        return sb.toString();
    }
}