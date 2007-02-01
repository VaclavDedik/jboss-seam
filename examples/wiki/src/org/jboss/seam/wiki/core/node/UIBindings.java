package org.jboss.seam.wiki.core.node;

import org.jboss.seam.annotations.Name;

import javax.faces.component.UIData;

/**
 * Can't use conversational components for UI binding in Seam... and @In(#{uiComponents['id']}) doesn't work
 * for some reason... and JSF stinks.
 */
@Name("uiBindings")
public class UIBindings {

    private UIData childNodeTable;
    public UIData getChildNodeTable() { return childNodeTable; }
    public void setChildNodeTable(UIData childNodeTable) { this.childNodeTable = childNodeTable; }

}
