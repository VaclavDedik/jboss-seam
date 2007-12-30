/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.wiki.core.model.Role;
import org.jboss.seam.wiki.core.search.metamodel.SearchRegistry;
import org.jboss.seam.wiki.core.search.metamodel.SearchableEntity;
import org.jboss.seam.wiki.core.upload.importers.metamodel.Importer;
import org.jboss.seam.wiki.core.upload.importers.metamodel.ImporterRegistry;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import java.io.Serializable;
import java.util.List;

@Name("converters")
@Scope(ScopeType.APPLICATION)

public class Converters {

    public String[] getMonthNames() {
        return new String[]{"NULL","January","February","March","April","May","June","July","August","September","October","November","December"};
    }

    @Name("importerConverter")
    @org.jboss.seam.annotations.faces.Converter(forClass = Importer.class)
    public static class ImporterConverter implements Converter, Serializable {

        public Object getAsObject(FacesContext arg0,
                                  UIComponent arg1,
                                  String arg2) throws ConverterException {
            if (arg2 == null) return null;
            ImporterRegistry importerRegistry = (ImporterRegistry)Component.getInstance("importerRegistry");
            return importerRegistry.getImportersByName().get(arg2);
        }

        public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) throws ConverterException {
            if (arg2 instanceof Importer) {
                return ((Importer)arg2).getComponentName();
            } else {
                return null;
            }
        }
    }

    @Name("searchableEntityConverter")
    @org.jboss.seam.annotations.faces.Converter(forClass = SearchableEntity.class)
    public static class SearchableEntityConverter implements Converter, Serializable {

        public Object getAsObject(FacesContext arg0,
                                  UIComponent arg1,
                                  String arg2) throws ConverterException {
            if (arg2 == null) return null;
            SearchRegistry searchRegistry = (SearchRegistry)Component.getInstance("searchRegistry");
            return searchRegistry.getSearchableEntitiesByName().get(arg2);
        }

        public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) throws ConverterException {
            if (arg2 instanceof SearchableEntity) {
                return ((SearchableEntity)arg2).getClazz().getName();
            } else {
                return null;
            }
        }
    }

    @Name("accessLevelConverter")
    @org.jboss.seam.annotations.faces.Converter(forClass = Role.AccessLevel.class)
    public static class AccessLevelConverter implements Converter, Serializable {

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

        /* TODO: Fixme or deleteme
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
        */

    }

}
