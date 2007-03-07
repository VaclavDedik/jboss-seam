package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.wiki.core.model.Node;

public class WikiLink {
    Node node;
    boolean broken = false;
    String url;
    String description;
    boolean external;

    public WikiLink(Node node, boolean broken, String url, String description, boolean external) {
        this.node = node;
        this.url = url;
        this.broken = broken;
        this.description = description;
        this.external = external;
    }
    public Node getNode() { return node; }
    public boolean isBroken() { return broken; }
    public String getUrl() { return url; }
    public String getDescription() { return description; }
    public boolean isExternal() { return external; }
    public String toString() {
        return "Description: " + description + " URL: " + url;
    }
}
