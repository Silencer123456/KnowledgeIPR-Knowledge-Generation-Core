package kiv.zcu.knowledgeipr.core.search.category.data;

import kiv.zcu.knowledgeipr.core.search.category.tree.TreeNode;

import java.util.ArrayList;

/**
 * Class loads sample categories and creates a category tree.
 */
public class SampleCategoryTreeReader implements ICategoryTreeReader {
    @Override
    public TreeNode<Category> read() {
        TreeNode<Category> root = new TreeNode<>(new Category("Categories"));
        {
            TreeNode<Category> node0 = root.addChild(new Category("Vehicles"));
            {
                TreeNode<Category> node00 = node0.addChild(new Category("Wheels", new ArrayList<String>() {{
                    add("rims");
                    add("discs");
                    add("hubs");
                    add("axles");
                }}));
                {
                    node00.addChild(new Category("rims"));
                    node00.addChild(new Category("discs"));
                    node00.addChild(new Category("hubs"));
                    node00.addChild(new Category("axles"));
                }
                TreeNode<Category> node01 = node0.addChild(new Category("Tyres"));
                TreeNode<Category> node02 = node0.addChild(new Category("Suspension"));
                TreeNode<Category> node03 = node0.addChild(new Category("Windows"));
                TreeNode<Category> node04 = node0.addChild(new Category("Brakes"));
                TreeNode<Category> node05 = node0.addChild(new Category("Land vehicles"));
            }
            TreeNode<Category> node1 = root.addChild(new Category("Agriculture"));
        }

        return root;
    }
}
