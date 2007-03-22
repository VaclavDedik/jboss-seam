package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.wiki.core.model.Role;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.security.Identity;
import org.richfaces.component.TreeNode;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class Converters {
 
    @Name("accessLevelConverter")
    @org.jboss.seam.annotations.jsf.Converter(forClass = Role.AccessLevel.class)
    public static class AccessLevelConverter implements Converter, Serializable {

        @Transactional
        public Object getAsObject(FacesContext arg0,
                                  UIComponent arg1,
                                  String arg2) throws ConverterException {
            if (arg2 == null) return null;
            try {
                List<Role.AccessLevel> accessLevels = (List<Role.AccessLevel>)Component.getInstance("accessLevelsList");
                return accessLevels.get(accessLevels.indexOf(new Role.AccessLevel(Integer.valueOf(arg2), null)));
            } catch (NumberFormatException e) {
                throw new ConverterException("Cannot find selected access level", e);
            }
        }

        public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) throws ConverterException {
            if (arg2 instanceof Role.AccessLevel) {
                Role.AccessLevel accessLevel = (Role.AccessLevel)arg2;
                return accessLevel.getAccessLevel().toString();
            } else {
                return null;
            }
        }
    }

    @Name("treeNodeAdapter")
    public static class TreeNodeAdapter {

        @Factory(value = "writableDirectoryTree", scope = ScopeType.CONVERSATION, autoCreate = true)
        public TreeNode loadWritableDirectoryTree() {
            Directory wikiroot = (Directory) Component.getInstance("restrictedWikiRoot");
            return new WikiTreeNode(wikiroot, true);
        }
        @Factory(value = "readableDirectoryTree", scope = ScopeType.CONVERSATION, autoCreate = true)
        public TreeNode loadReadableDirectoryTree() {
            Directory wikiroot = (Directory) Component.getInstance("restrictedWikiRoot");
            return new WikiTreeNode(wikiroot, false);
        }
        class WikiTreeNode implements TreeNode {
            boolean onlyWritableChildren;
            private Node wikiNode;
            private Map<Object, TreeNode> childrenMap = new LinkedHashMap<Object,TreeNode>();

            public WikiTreeNode(Node wikiNode, boolean onlyWritableChildren) {
                if (wikiNode != null) {
                    this.wikiNode = wikiNode;
                    this.onlyWritableChildren = onlyWritableChildren;
                    for (Node childNode : wikiNode.getChildren()) {
                        if (!WikiUtil.isDirectory(childNode)) continue;
                        if (onlyWritableChildren && !Identity.instance().hasPermission("Node", "edit", childNode)) continue;
                        childrenMap.put(childNode.getId(), new WikiTreeNode(childNode, onlyWritableChildren));
                    }
                }
            }
            public Object getData() { return wikiNode; }
            public void setData(Object node) { this.wikiNode = (Node)node; }

            public boolean isLeaf() {
                return childrenMap.size() == 0;
            }
            public Iterator getChildren() {
                return childrenMap.entrySet().iterator();
            }
            public TreeNode getChild(Object identifier) {
                return childrenMap.get( identifier );
            }
            public void addChild(Object identifier, TreeNode treeNode) {
                childrenMap.put(identifier, treeNode);
            }
            public void removeChild(Object identifier) {
                // Immutable
            }

            public TreeNode getParent() {
                return new WikiTreeNode(wikiNode.getParent(), onlyWritableChildren);
            }

            public void setParent(TreeNode treeNode) {
                // Immutable
            }
        }

    }

}
