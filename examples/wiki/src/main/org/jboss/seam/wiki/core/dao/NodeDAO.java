/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.dao;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.nestedset.NestedSetNode;
import org.jboss.seam.wiki.core.nestedset.NestedSetNodeWrapper;
import org.jboss.seam.wiki.core.nestedset.NestedSetResultTransformer;
import org.jboss.seam.wiki.core.nestedset.NestedSetNodeDuplicator;
import org.jboss.seam.Component;
import org.hibernate.Session;
import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Query;
import org.hibernate.transform.DistinctRootEntityResultTransformer;
import org.hibernate.criterion.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
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

    // Most of the DAO methods use this
    @In protected EntityManager restrictedEntityManager;

    // Some run unrestricted (e.g. internal unique key validation of wiki names)
    // Make sure that these methods do not return detached objects!
    @In protected EntityManager entityManager;

    public void flushRegularEntityManager() {
        restrictedEntityManager.flush();
    }

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

    public List<Document> findDocumentsInDirectoryOrderByCreatedOn(Directory directory, int firstResult, int maxResults) {
        //noinspection unchecked
        return (List<Document>)restrictedEntityManager
                .createQuery("select d from Document d where d.parent = :parentDir order by d.createdOn desc")
                .setParameter("parentDir", directory)
                .setHint("org.hibernate.comment", "Find documents in directory order by createdOn")
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .getResultList();
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
        Node historicalNode = (Node)getSession().get("HistoricalDocument", historyId);
        getSession().evict(historicalNode);
        return historicalNode;
    }

    public void persistHistoricalNode(Node historicalNode) {
        // TODO: Ugh, concatenating class names to get the entity name?!
        getSession().persist("Historical"+historicalNode.getClass().getSimpleName(), historicalNode);
        getSession().flush();
        getSession().evict(historicalNode);
    }

    @SuppressWarnings({"unchecked"})
    public List<Node> findHistoricalNodes(Node node) {
        if (node == null) return null;
        return getSession().createQuery("select n from HistoricalNode n where n.nodeId = :nodeId order by n.revision desc")
                            .setParameter("nodeId", node.getId())
                            .list();
    }

    public Long findNumberOfHistoricalNodes(Node node) {
        if (node == null) return null;
        return (Long)getSession().createQuery("select count(n) from HistoricalNode n where n.nodeId = :nodeId")
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

    public Map<Long,Long> findCommentCount(Directory directory) {
        //noinspection unchecked
        List<Object[]> result = restrictedEntityManager
                .createQuery("select n.nodeId, count(c) from Node n, Comment c where c.document = n and n.parent = :parent group by n.nodeId")
                .setParameter("parent", directory)
                .setHint("org.hibernate.comment", "Find comment cound for all nodes in directory")
                .getResultList();

        Map<Long,Long> resultMap = new HashMap<Long,Long>(result.size());
        for (Object[] objects : result) {
            resultMap.put((Long)objects[0], (Long)objects[1]);
        }
        return resultMap;
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

    // TODO: Not used
    public NestedSetNodeWrapper<Node> findWithCommentCountOrderedByCreatedOn(Node startNode, Long maxDepth, Long flattenToLevel) {
        // Needs to be equals() safe (SortedSet):
        // - compare by creation date (note: don't compare data/timestamp), if equal
        // - compare by name, if equal
        // - compare by id
        Comparator<NestedSetNodeWrapper<Node>> comp =
            new Comparator<NestedSetNodeWrapper<Node>>() {
                public int compare(NestedSetNodeWrapper<Node> o1, NestedSetNodeWrapper<Node> o2) {
                    Node node1 = o1.getWrappedNode();
                    Node node2 = o2.getWrappedNode();
                    if (node1.getCreatedOn().getTime() != node2.getCreatedOn().getTime()) {
                        return node1.getCreatedOn().getTime() > node2.getCreatedOn().getTime() ? -1 : 1;
                    } else if (node1.getName().compareTo(node2.getName()) != 0) {
                        return node1.getName().compareTo(node2.getName());
                    }
                    return node1.getId().compareTo(node2.getId());
                }
            };

        Map<String, String> additionalProjections = new LinkedHashMap<String, String>();
        additionalProjections.put("commentCount", "(select count(c) from Comment c where c.document.id = n1.id)");

        NestedSetNodeWrapper<Node> startNodeWrapper = new NestedSetNodeWrapper<Node>(startNode, comp);
        NestedSetResultTransformer<Node> transformer =
                new NestedSetResultTransformer<Node>(startNodeWrapper, flattenToLevel, additionalProjections);

        appendNestedSetNodes(transformer, maxDepth, false);
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

        Query nestedSetQuery = getSession().createQuery(queryString.toString());
        nestedSetQuery.setParameter("thread", startNode.getNsThread());
        nestedSetQuery.setParameter("startLeft", startNode.getNsLeft());
        nestedSetQuery.setParameter("startRight", startNode.getNsRight());
        if (showAdminOnly) nestedSetQuery.setParameter("adminUser", Component.getInstance("adminUser"));
        if (maxDepth != null) nestedSetQuery.setParameter("maxDepth", maxDepth);

        nestedSetQuery.setCacheable(true);

        nestedSetQuery.setResultTransformer(transformer);
        nestedSetQuery.list(); // Append all children hierarchically to the startNodeWrapper
    }

    public <N extends Node> List<N> findWithParent(Class<N> nodeType, Directory directory, Node ignoreNode,
                                                   String orderByProperty, boolean orderDescending, long firstResult, long maxResults) {

        Criteria crit = prepareCriteria(nodeType, orderByProperty, orderDescending);
        crit.add(Restrictions.eq("parent", directory));
        if (ignoreNode != null)
            crit.add(Restrictions.ne("id", ignoreNode.getId()));
        if ( !(firstResult == 0 && maxResults == 0) )
            crit.setFirstResult(Long.valueOf(firstResult).intValue()).setMaxResults(Long.valueOf(maxResults).intValue());
        //noinspection unchecked
        return crit.list();
    }

    public int getRowCountWithParent(Class nodeType, Directory directory, Node ignoreNode) {
        Criteria crit = prepareCriteria(nodeType, null, false);
        crit.add(Restrictions.eq("parent", directory));
        if (ignoreNode != null)
            crit.add(Restrictions.ne("id", ignoreNode.getId()));
        return getRowCount(crit);
    }

    public <N extends Node> int getRowCountByExample(N exampleNode, String... ignoreProperty) {
        Criteria crit = prepareCriteria(exampleNode.getClass(), null, false);
        appendExample(crit, exampleNode, ignoreProperty);
        return getRowCount(crit);
    }

    private int getRowCount(Criteria criteria) {
        ScrollableResults cursor = criteria.scroll();
        cursor.last();
        int count = cursor.getRowNumber() + 1;
        cursor.close();
        return count;
    }

    private Criteria prepareCriteria(Class rootClass, String orderByProperty, boolean orderDescending) {
        Criteria crit = getSession().createCriteria(rootClass);
        if (orderByProperty != null)
                crit.addOrder( orderDescending ? Order.desc(orderByProperty) : Order.asc(orderByProperty) );
        return crit.setResultTransformer(new DistinctRootEntityResultTransformer());
    }

    private <N extends Node> void appendExample(Criteria criteria, N exampleNode, String... ignoreProperty) {
        Example example =  Example.create(exampleNode).enableLike(MatchMode.ANYWHERE).ignoreCase();
        for (String s : ignoreProperty) example.excludeProperty(s);
        criteria.add(example);
    }

    private Session getSession() {
        restrictedEntityManager.joinTransaction();
        return ((Session)((org.jboss.seam.persistence.EntityManagerProxy) restrictedEntityManager).getDelegate());
    }
}
