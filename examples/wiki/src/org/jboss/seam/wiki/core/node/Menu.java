package org.jboss.seam.wiki.core.node;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.ArrayList;

@Name("menu")
@Scope(ScopeType.CONVERSATION)
public class Menu {

    @In(create = true)
    protected EntityManager entityManager;

    @In(create = true)
    private Directory wikiRoot;

    private List<MenuItem> items;
    public List<MenuItem> getItems() {
        if (items == null) refreshMenuItems();
        return items;
    }

    /** 
     * This is very inefficient. There really is no better way if we want recursively have
     * all documents and directories with isMenuItem() in the main menu. Not even a direct
     * SQL query would help (multicolumn ordering would require by PK, not good). If this
     * can't be made performant with caching, we need to replace it with a simple one
     * or two level menu item search. Currently optimizing with batch fetching, future
     * implementation might use a nested set approach (we need one anyway for recursive
     * deletion of subtrees).
     */
    @Observer("Nodes.directoryStructureModified")
    @Transactional
    public void refreshMenuItems() {
        items = new ArrayList<MenuItem>();
        entityManager.joinTransaction();
        for(Node area : wikiRoot.getChildren())
            addNodesToMenuTree(items, 0, area);
    }

    // Recursive
    private void addNodesToMenuTree(List<MenuItem> menuItems, int i, Node node) {
        MenuItem menuItem = new MenuItem(node);
        menuItem.setLevel(i);
        if (node.isMenuItem()) menuItems.add(menuItem); // Check flag in-memory
        if (node.getChildren() != null && node.getChildren().size() > 0) {
            i++;
            for (Node child : node.getChildren()) {
                if (i > 1)
                    // Flatten the menu tree into two levels (simple display)
                    addNodesToMenuTree(menuItems, i, child);
                else
                    addNodesToMenuTree(menuItem.getSubItems(), i, child);
            }
        }
    }

    public class MenuItem{
        private Node node;
        private int level;
        private List<MenuItem> subItems = new ArrayList<MenuItem>();

        public MenuItem(Node node) { this.node = node; }

        public Node getNode() { return node; }
        public void setNode(Node node) { this.node = node; }

        public int getLevel() { return level; }
        public void setLevel(int level) { this.level = level; }

        public List<MenuItem> getSubItems() { return subItems; }
        public void setSubItems(List<MenuItem> subItems) { this.subItems = subItems; }
    }

    
}
