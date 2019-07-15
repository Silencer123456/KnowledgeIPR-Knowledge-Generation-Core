package kiv.zcu.knowledgeipr.core.query.category.datastructure;

import java.util.ArrayList;
import java.util.List;

public class SampleCategories {

    private TreeNode<String> root;

    public SampleCategories() {
        createCategories();
    }

    // TODO: Load from external source
    public TreeNode<String> createCategories() {
        root = new TreeNode<>("Categories");
        {
            TreeNode<String> node0 = root.addChild("Vehicles");
            {
                TreeNode<String> node00 = node0.addChild("Wheels");
                TreeNode<String> node01 = node0.addChild("Tyres");
                TreeNode<String> node02 = node0.addChild("Suspension");
                TreeNode<String> node03 = node0.addChild("Windows");
                TreeNode<String> node04 = node0.addChild("Brakes");
                TreeNode<String> node05 = node0.addChild("Land vehicles");
            }
        }

        return root;
    }

    public boolean containsCategory(String category) {
        Comparable<String> searchCriteria = treeData -> {
            if (treeData == null)
                return 1;
            boolean nodeOk = treeData.contains(category);
            return nodeOk ? 0 : 1;
        };

        TreeNode<String> found = root.findTreeNode(searchCriteria);

        return found != null;
    }

    public List<String> getNodesAtLevel(int level) {
        List<String> nodes = new ArrayList<>();

        TreeNode<String> treeRoot = createCategories();
        for (TreeNode<String> node : treeRoot) {
            //String indent = createIndent(node.getLevel());
            //System.out.println(indent + node.data);
            if (node.getLevel() == level) {
                nodes.add(node.data);
            }
        }

        return nodes;
    }

    public TreeNode<String> getRoot() {
        return root;
    }

    public String getTreeAsString() {
        StringBuilder sb = new StringBuilder();
        for (TreeNode<String> node : root) {
            String indent = createIndent(node.getLevel());
            sb.append(indent).append(node.data).append("\n");
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
