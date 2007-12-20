package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.core.model.WikiNode;

import java.util.*;
import java.io.Serializable;

@Name("clipboard")
@Scope(ScopeType.SESSION)
@AutoCreate
public class Clipboard implements Serializable {

    private Map<WikiNode, Boolean> items = new LinkedHashMap<WikiNode, Boolean>();

    public Set<WikiNode> getItems() {
        return items.keySet();
    }

    public List<WikiNode> getItemsAsList() {
        return new ArrayList<WikiNode>(getItems());
    }

    public void clear() {
        items.clear();
    }

    public void add(WikiNode node, Boolean cut) {
        items.put(node, cut);
    }

    public boolean isCut(Long nodeId) {
        for (WikiNode wikiNode : items.keySet()) {
            if (wikiNode.getId().equals(nodeId) && items.get(wikiNode)) return true;
        }
        return false;
    }

    public boolean isContainsCutFromDirectory(Long dirId) {
        for (WikiNode wikiNode : items.keySet()) {
            if (wikiNode.getParent().getId().equals(dirId)) return true;
        }
        return false;
    }
}
