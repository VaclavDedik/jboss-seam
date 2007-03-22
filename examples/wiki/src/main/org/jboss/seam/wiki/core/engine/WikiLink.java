package org.jboss.seam.wiki.core.engine;

import org.jboss.seam.wiki.core.model.Node;

/**
 * Simple value holder for link resolution and rendering.
 *
 * @author Christian Bauer
 */
public class WikiLink {
    Node node;
    boolean requiresUpdating = false;
    String url;
    String description;
    boolean broken = false;
    boolean external = false;

    public WikiLink(boolean broken, boolean external) {
        this.broken = broken;
        this.external = external;
    }
    public Node getNode() { return node; }
    public void setNode(Node node) { this.node = node; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isBroken() { return broken; }
    public boolean isExternal() { return external; }

    public boolean isRequiresUpdating() { return requiresUpdating; }
    public void setRequiresUpdating(boolean requiresUpdating) { this.requiresUpdating = requiresUpdating; }

    public String toString() {
        return "Node:" + node + " Description: " + description + " URL: " + url;
    }
}
