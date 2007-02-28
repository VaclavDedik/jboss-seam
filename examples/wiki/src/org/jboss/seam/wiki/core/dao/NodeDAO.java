package org.jboss.seam.wiki.core.dao;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.wiki.core.node.Node;
import org.jboss.seam.wiki.core.node.Directory;
import org.jboss.seam.wiki.core.node.Document;
import org.jboss.seam.wiki.core.node.File;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

/**
 * DAO for Nodes.
 * <p>
 * The primary reason why this DAO exists is the broken JPA specification. There is no reason
 * why query.getSingleResult() should throw any exception if the query result is empty. It should
 * just return <tt>null</tt>, like Hibernates query.uniqueResult() method. So instead of using
 * the EntityManager directly, most users, like me, will outsource this exception wrapping into
 * a DAO. Hey, this sounds like a job for Spring? Or maybe we should fix the specification...
 *
 * @author Christian Bauer
 *
 */
@Name("nodeDAO")
@AutoCreate
@Transactional
public class NodeDAO {

    @In
    protected EntityManager entityManager;

    @In
    Directory wikiRoot;

    public Node findNode(Long nodeId) {
        entityManager.joinTransaction();
        try {
            return entityManager.find(Node.class, nodeId);
        } catch (EntityNotFoundException ex) {
        }
        return null;
    }

    public Node findNodeInArea(Long areaNumber, String wikiname) {
        entityManager.joinTransaction();

        try {
            return (Node) entityManager
                    .createQuery("select n from Node n where n.areaNumber = :areaNumber and n.wikiname = :wikiname")
                    .setParameter("areaNumber", areaNumber)
                    .setParameter("wikiname", wikiname)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    public Node findNodeInDirectory(Directory directory, String wikiname) {
        entityManager.joinTransaction();

        try {
            return (Node) entityManager
                    .createQuery("select n from Node n where n.parent = :parentDir and n.wikiname = :wikiname")
                    .setParameter("parentDir", directory)
                    .setParameter("wikiname", wikiname)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    public Document findDocumentInArea(Long areaNumber, String wikiname) {
        entityManager.joinTransaction();

        try {
            return (Document) entityManager
                    .createQuery("select d from Document d where d.areaNumber = :areaNumber and d.wikiname = :wikiname")
                    .setParameter("areaNumber", areaNumber)
                    .setParameter("wikiname", wikiname)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    public Directory findDirectoryInArea(Long areaNumber, String wikiname) {
        entityManager.joinTransaction();

        try {
            return (Directory) entityManager
                    .createQuery("select d from Directory d where d.areaNumber = :areaNumber and d.wikiname = :wikiname")
                    .setParameter("areaNumber", areaNumber)
                    .setParameter("wikiname", wikiname)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    public Directory findArea(String wikiname) {
        entityManager.joinTransaction();

        try {
            return (Directory) entityManager
                    .createQuery("select d from Directory d where d.parent = :root and d.wikiname = :wikiname")
                    .setParameter("root", wikiRoot)
                    .setParameter("wikiname", wikiname)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    // I need these methods because find() is broken, e.g. find(Document,1) would return a Directory if the
    // persistence context contains a directory with id 1... even more annoying, I need to catch NoResultException,
    // so there really is no easy and correct way to look for the existence of a row.
    // TODO: A new Hibernate version should fix find()/get() - the old JBoss AS 4.0.5 version is broken
    // ... or is it not: http://opensource.atlassian.com/projects/hibernate/browse/HHH-2352

    public Document findDocument(Long documentId) {
        entityManager.joinTransaction();

        try {
            return (Document) entityManager
                    .createQuery("select d from Document d where d.id = :id")
                    .setParameter("id", documentId)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    public Directory findDirectory(Long directoryId) {
        entityManager.joinTransaction();

        try {
            return (Directory) entityManager
                    .createQuery("select d from Directory d where d.id = :id")
                    .setParameter("id", directoryId)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }

    public File findFile(Long fileId) {
        entityManager.joinTransaction();

        try {
            return (File) entityManager
                    .createQuery("select f from File f where f.id = :id")
                    .setParameter("id", fileId)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        return null;
    }
}
