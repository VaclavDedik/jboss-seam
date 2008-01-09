package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.*;
import java.io.Serializable;

@Name("clipboard")
@Scope(ScopeType.SESSION)
@AutoCreate
public class Clipboard implements Serializable {

    private Map<Long, Boolean> items = new LinkedHashMap<Long, Boolean>();

    public Set<Long> getItems() {
        return items.keySet();
    }

    public List<Long> getItemsAsList() {
        return new ArrayList<Long>(getItems());
    }

    public void clear() {
        items.clear();
    }

    public void add(Long nodeId, Boolean cut) {
        items.put(nodeId, cut);
    }

    public boolean isCut(Long nodeId) {
        return items.containsKey(nodeId) && items.get(nodeId);
    }

}
