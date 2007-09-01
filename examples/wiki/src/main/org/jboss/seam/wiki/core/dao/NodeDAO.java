/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.dao;

import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.nestedset.NestedSetNode;
import org.jboss.seam.wiki.core.nestedset.NestedSetNodeWrapper;
import org.jboss.seam.wiki.core.nestedset.NestedSetResultTransformer;
import org.jboss.seam.wiki.core.nestedset.NestedSetNodeDuplicator;
import org.jboss.seam.wiki.preferences.PreferenceProvider;
import org.jboss.seam.Component;
import org.jboss.seam.log.Log;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.*;

/**
 * DAO for nodes, transparently respects security access levels.
 * <p>
 * All node access should go through this component, this component knows
 * about access levels because it relies on a restricted (filtered) Entitymanager.
 *
 * @author Christian Bauer
 *
 */
@Name("nodeDAO")
@AutoCreate
@Transactional
public class NodeDAO {

    @Logger
    static Log log;

    // Most of the DAO methods use this
    @In protected EntityManager restrictedEntityManager;

    // Some run unrestricted (e.g. internal unique key validation of wiki names)
    // Make sure that these methods do not return detached objects!
    @In protected EntityManager entityManager;

    public void makePersistent(Node node) {
        entityManager.joinTransaction();
        entityManager.persist(node);
    }

    public Node findNode(Long nodeId) {
        restrictedEntityManager.joinTransaction();
        try {
            return (Node) restrictedEntityManager
                    .createQuery("select n from Node n where n.id = :nodeId")
                    .setParameter("nodeId", nodeId)
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    public Node findNodeInArea(Long areaNumber, String wikiname) {
        return findNodeInArea(areaNumber, wikiname, restrictedEntityManager);
    }

    private Node findNodeInArea(Long areaNumber, String wikiname, EntityManager em) {
        em.joinTransaction();

        try {
            return (Node) em
                    .createQuery("select n from Node n where n.areaNumber = :areaNumber and n.wikiname = :wikiname")
                    .setParameter("areaNumber", areaNumber)
                    .setParameter("wikiname", wikiname)
                    .setHint("org.hibernate.comment", "Find node in area")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    public Document findDocumentInArea(Long areaNumber, String wikiname) {
        restrictedEntityManager.joinTransaction();

        try {
            return (Document) restrictedEntityManager
                    .createQuery("select d from Document d where d.areaNumber = :areaNumber and d.wikiname = :wikiname")
                    .setParameter("areaNumber", areaNumber)
                    .setParameter("wikiname", wikiname)
                    .setHint("org.hibernate.comment", "Find document in area")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    public Directory findDirectoryInArea(Long areaNumber, String wikiname) {
        restrictedEntityManager.joinTransaction();

        try {
            return (Directory) restrictedEntityManager
                    .createQuery("select d from Directory d where d.areaNumber = :areaNumber and d.wikiname = :wikiname")
                    .setParameter("areaNumber", areaNumber)
                    .setParameter("wikiname", wikiname)
                    .setHint("org.hibernate.comment", "Find directory in area")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    public Directory findArea(String wikiname) {
        restrictedEntityManager.joinTransaction();

        try {
            return (Directory) restrictedEntityManager
                    .createQuery("select d from Directory d where d.parent = :root and d.wikiname = :wikiname")
                    .setParameter("root", Component.getInstance("wikiRoot"))
                    .setParameter("wikiname", wikiname)
                    .setHint("org.hibernate.comment", "Find area by wikiname")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    public Directory findArea(Long areaNumber) {
        restrictedEntityManager.joinTransaction();

        try {
            return (Directory) restrictedEntityManager
                    .createQuery("select d from Directory d where d.parent = :root and d.areaNumber = :areaNumber")
                    .setParameter("root", Component.getInstance("wikiRoot"))
                    .setParameter("areaNumber", areaNumber)
                    .setHint("org.hibernate.comment", "Find area by area number")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    public List<Document> findDocumentsOrderByLastModified(int maxResults) {
        //noinspection unchecked
        return (List<Document>)restrictedEntityManager
                .createQuery("select d from Document d order by d.lastModifiedOn desc")
                .setHint("org.hibernate.comment", "Find documents order by lastModified")
                .setMaxResults(maxResults)
                .getResultList();
    }

    public Node findHistoricalNode(Long historyId) {
        Node historicalNode = (Node)getSession(true).get("HistoricalDocument", historyId);
        getSession(true).evict(historicalNode);
        return historicalNode;
    }

    public void persistHistoricalNode(Node historicalNode) {
        // TODO: Ugh, concatenating class names to get the entity name?!
        getSession(true).persist("Historical"+historicalNode.getClass().getSimpleName(), historicalNode);
        getSession(true).flush();
        getSession(true).evict(historicalNode);
    }

    @SuppressWarnings({"unchecked"})
    public List<Node> findHistoricalNodes(Node node) {
        if (node == null) return null;
        return getSession(true).createQuery("select n from HistoricalNode n where n.nodeId = :nodeId order by n.revision desc")
                                .setParameter("nodeId", node.getId())
                                .list();
    }

    public Long findNumberOfHistoricalNodes(Node node) {
        if (node == null) return null;
        return (Long)getSession(true).createQuery("select count(n) from HistoricalNode n where n.nodeId = :nodeId")
                                  .setParameter("nodeId", node.getId())
                                  .uniqueResult();

    }
    
    // Multi-row constraint validation
    public boolean isUniqueWikiname(Node node) {
        Node foundNode = findNodeInArea(node.getParent().getAreaNumber(), node.getWikiname(), entityManager);
        if (foundNode == null) {
            return true;
        } else {
            return node.getId() != null && node.getId().equals(foundNode.getId());
        }
    }

    public boolean isUniqueWikiname(Long areaNumber, String wikiname) {
        Node foundNode = findNodeInArea(areaNumber, wikiname, entityManager);
        return foundNode == null;
    }

    public Document findDocument(Long documentId) {
        restrictedEntityManager.joinTransaction();

        try {
            return (Document) restrictedEntityManager
                    .createQuery("select d from Document d where d.id = :id")
                    .setParameter("id", documentId)
                    .setHint("org.hibernate.comment", "Find document by id")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    public Directory findDirectory(Long directoryId) {
        restrictedEntityManager.joinTransaction();

        try {
            return (Directory) restrictedEntityManager
                    .createQuery("select d from Directory d where d.id = :id")
                    .setParameter("id", directoryId)
                    .setHint("org.hibernate.comment", "Find directory by id")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    public File findFile(Long fileId) {
        restrictedEntityManager.joinTransaction();

        try {
            return (File) restrictedEntityManager
                    .createQuery("select f from File f where f.id = :id")
                    .setParameter("id", fileId)
                    .setHint("org.hibernate.comment", "Find file by id")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    public Document findDefaultDocument(Node directory) {
        if (directory == null) return null;
        restrictedEntityManager.joinTransaction();
        try {
            return (Document) restrictedEntityManager
                    .createQuery("select doc from Document doc, Directory dir" +
                                 " where doc.id = dir.defaultDocument.id and dir.id = :did")
                    .setParameter("did", directory.getId())
                    .setHint("org.hibernate.comment", "Find default document")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    public void removeChildren(Directory dir) {

        // Find all nested documents so we can delete them one by one, updating the second-level cache
        StringBuilder queryString = new StringBuilder();

        queryString.append("select").append(" ");
        queryString.append("n1 as nestedSetNode").append(" ");
        queryString.append("from ").append(dir.getTreeSuperclassEntityName()).append(" n1, ");
        queryString.append(dir.getTreeSuperclassEntityName()).append(" n2 ");
        queryString.append("where n1.nsThread = :thread and n2.nsThread = :thread").append(" ");
        queryString.append("and n1.nsLeft between n2.nsLeft and n2.nsRight").append(" ");
        queryString.append("and n2.nsLeft > :startLeft and n2.nsRight < :startRight").append(" ");
        queryString.append("and n2.class = :clazz").append(" ");
        queryString.append("group by").append(" ");
        for (int i = 0; i < dir.getTreeSuperclassPropertiesForGrouping().length; i++) {
            queryString.append("n1.").append(dir.getTreeSuperclassPropertiesForGrouping()[i]);
            if (i != dir.getTreeSuperclassPropertiesForGrouping().length-1) queryString.append(", ");
        }
        queryString.append(" ");

        org.hibernate.Query nestedSetQuery = getSession(true).createQuery(queryString.toString());
        nestedSetQuery.setParameter("thread", dir.getNsThread());
        nestedSetQuery.setParameter("startLeft", dir.getNsLeft());
        nestedSetQuery.setParameter("startRight", dir.getNsRight());
        nestedSetQuery.setParameter("clazz", "DOCUMENT");

        getSession(true).disableFilter("accessLevelFilter"); // All of them
        List<Document> docs = nestedSetQuery.list();
        for (Document doc : docs) {
            log.debug("recursive directory delete, deleting: " + doc);
            getSession(true).delete(doc);
        }
        getSession(true).flush();
    }

    public NestedSetNodeWrapper<Node> findMenuItems(Node startNode, Long maxDepth, Long flattenToLevel, boolean showAdminOnly) {

        // Needs to be equals() safe (SortedSet):
        // - compare by display position, if equal
        // - compare by name, if equal
        // - compare by id
        Comparator<NestedSetNodeWrapper<Node>> comp =
            new Comparator<NestedSetNodeWrapper<Node>>() {
                public int compare(NestedSetNodeWrapper<Node> o1, NestedSetNodeWrapper<Node> o2) {
                    Node node1 = o1.getWrappedNode();
                    Node node2 = o2.getWrappedNode();
                    if (node1.getDisplayPosition().compareTo(node2.getDisplayPosition()) != 0) {
                        return node1.getDisplayPosition().compareTo(node2.getDisplayPosition());
                    } else if (node1.getName().compareTo(node2.getName()) != 0) {
                        return node1.getName().compareTo(node2.getName());
                    }
                    return node1.getId().compareTo(node2.getId());
                }
            };

        NestedSetNodeWrapper<Node> startNodeWrapper = new NestedSetNodeWrapper<Node>(startNode, comp);
        NestedSetResultTransformer<Node> transformer = new NestedSetResultTransformer<Node>(startNodeWrapper, flattenToLevel);

        // Make hollow copies for menu display so that changes to the model in the persistence context don't appear
        transformer.setNestedSetNodeDuplicator(
            new NestedSetNodeDuplicator<Node>() {
                public Node duplicate(Node nestedSetNode) {
                    Node copy = null;
                    if (nestedSetNode instanceof Document) {
                        copy = new Document((Document)nestedSetNode);
                    } else if (nestedSetNode instanceof Directory) {
                        copy = new Directory((Directory)nestedSetNode);
                    } else if (nestedSetNode instanceof File) {
                        copy = new File((File)nestedSetNode);
                    }
                    if (copy != null) {
                        copy.setId(nestedSetNode.getId());
                        copy.setParent(nestedSetNode.getParent());
                    }
                    return copy;
                }
            }
        );

        appendNestedSetNodes(transformer, maxDepth, showAdminOnly, "n1.menuItem = true");
        return startNodeWrapper;
    }

    public <N extends NestedSetNode> void appendNestedSetNodes(NestedSetResultTransformer<N> transformer,
                                                               Long maxDepth,
                                                               boolean showAdminOnly,
                                                               String... restrictionFragment) {

        N startNode = transformer.getRootWrapper().getWrappedNode();
        StringBuilder queryString = new StringBuilder();

        queryString.append("select").append(" ");
        queryString.append("count(n1.id) as nestedSetNodeLevel").append(", ");
        queryString.append("n1 as nestedSetNode").append(" ");
        for (Map.Entry<String, String> entry : transformer.getAdditionalProjections().entrySet()) {
            queryString.append(", ").append(entry.getValue()).append(" as ").append(entry.getKey()).append(" ");
        }
        queryString.append("from ").append(startNode.getTreeSuperclassEntityName()).append(" n1, ");
        queryString.append(startNode.getTreeSuperclassEntityName()).append(" n2 ");
        queryString.append("where n1.nsThread = :thread and n2.nsThread = :thread").append(" ");
        queryString.append("and n1.nsLeft between n2.nsLeft and n2.nsRight").append(" ");
        queryString.append("and n2.nsLeft > :startLeft and n2.nsRight < :startRight").append(" ");
        if (showAdminOnly) {
            queryString.append("and n1.createdBy = :adminUser").append(" ");
        }
        for (String fragment: restrictionFragment) {
            queryString.append("and ").append(fragment).append(" ");
        }
        queryString.append("group by").append(" ");
        for (int i = 0; i < startNode.getTreeSuperclassPropertiesForGrouping().length; i++) {
            queryString.append("n1.").append(startNode.getTreeSuperclassPropertiesForGrouping()[i]);
            if (i != startNode.getTreeSuperclassPropertiesForGrouping().length-1) queryString.append(", ");
        }
        queryString.append(" ");

        if (maxDepth != null) {
            queryString.append("having count(n1.id) <= :maxDepth").append(" ");
        }

        queryString.append("order by n1.nsLeft");

        org.hibernate.Query nestedSetQuery = getSession(true).createQuery(queryString.toString());
        nestedSetQuery.setParameter("thread", startNode.getNsThread());
        nestedSetQuery.setParameter("startLeft", startNode.getNsLeft());
        nestedSetQuery.setParameter("startRight", startNode.getNsRight());
        if (showAdminOnly) nestedSetQuery.setParameter("adminUser", Component.getInstance("adminUser"));
        if (maxDepth != null) nestedSetQuery.setParameter("maxDepth", maxDepth);

        nestedSetQuery.setCacheable(true);

        nestedSetQuery.setResultTransformer(transformer);
        nestedSetQuery.list(); // Append all children hierarchically to the startNodeWrapper
    }


    private Session getSession(boolean restricted) {
        if (restricted) {
            restrictedEntityManager.joinTransaction();
            return ((Session)((org.jboss.seam.persistence.EntityManagerProxy) restrictedEntityManager).getDelegate());
        } else {
            entityManager.joinTransaction();
            return ((Session)((org.jboss.seam.persistence.EntityManagerProxy) entityManager).getDelegate());
        }
    }
}
