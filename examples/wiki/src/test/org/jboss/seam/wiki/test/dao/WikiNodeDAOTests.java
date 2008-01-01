/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.dao;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiFile;
import org.jboss.seam.wiki.core.model.WikiComment;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

import java.util.List;

public class WikiNodeDAOTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void findDocumentById() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                WikiNodeDAO dao = (WikiNodeDAO)getInstance("wikiNodeDAO");
                WikiDocument d = dao.findWikiDocument(6l);
                assert d.getName().equals("One");
            }
        }.run();
    }

    @Test
    public void findDefaultFile() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                WikiNodeDAO dao = (WikiNodeDAO)getInstance("wikiNodeDAO");
                WikiDirectory dir = dao.findWikiDirectory(3l);
                WikiFile d = dao.findDefaultWikiFile(dir);
                assert d.getName().equals("One");
            }
        }.run();
    }

    // TODO: This can go away soon, see WikiRequestResolver
    @Test
    public void findDefaultDocument() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                WikiNodeDAO dao = (WikiNodeDAO)getInstance("wikiNodeDAO");
                WikiDirectory dir = dao.findWikiDirectory(3l);
                WikiDocument d = dao.findDefaultDocument(dir);
                assert d.getName().equals("One");
            }
        }.run();
    }

    @Test
    public void findDocumentInArea() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                WikiNodeDAO dao = (WikiNodeDAO)getInstance("wikiNodeDAO");
                WikiDocument d = dao.findWikiDocumentInArea(3l, "Two");
                assert d.getName().equals("Two");
                assert d.getId().equals(7l);
            }
        }.run();
    }

    @Test
    public void findDocumentsOrderByLastModified() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                WikiNodeDAO dao = (WikiNodeDAO)getInstance("wikiNodeDAO");
                List<WikiDocument> result = dao.findWikiDocumentsOrderByLastModified(2);
                assert result.size() == 2;
                assert result.get(0).getId().equals(6l);
                assert result.get(1).getId().equals(7l);
            }
        }.run();
    }

    @Test
    public void findDirectoryById() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                WikiNodeDAO dao = (WikiNodeDAO)getInstance("wikiNodeDAO");
                WikiDirectory d = dao.findWikiDirectory(1l);
                assert d.getName().equals("AAA");
            }
        }.run();
    }

    @Test
    public void findDirectoryInArea() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                WikiNodeDAO dao = (WikiNodeDAO)getInstance("wikiNodeDAO");
                WikiDirectory d = dao.findWikiDirectoryInArea(3l, "DDD");
                assert d.getName().equals("DDD");
                assert d.getId().equals(4l);
            }
        }.run();
    }

    @Test
    public void findAreaByWikiname() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                WikiNodeDAO dao = (WikiNodeDAO)getInstance("wikiNodeDAO");
                WikiDirectory d = dao.findArea("BBB");
                assert d.getName().equals("BBB");
                assert d.getId().equals(2l);
            }
        }.run();
    }

    @Test
    public void findAreaByNumber() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                WikiNodeDAO dao = (WikiNodeDAO)getInstance("wikiNodeDAO");
                WikiDirectory d = dao.findArea(2l);
                assert d.getName().equals("BBB");
                assert d.getId().equals(2l);
            }
        }.run();
    }

    @Test
    public void isUniqueWikinameTrue() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                WikiNodeDAO dao = (WikiNodeDAO)getInstance("wikiNodeDAO");
                Boolean result = dao.isUniqueWikiname(3l, "Foobar");
                assert result;
            }
        }.run();
    }

    @Test
    public void isUniqueWikinameFalse() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                WikiNodeDAO dao = (WikiNodeDAO)getInstance("wikiNodeDAO");
                Boolean result = dao.isUniqueWikiname(3l, "One");
                assert !result;
            }
        }.run();
    }

    @Test
    public void isUniqueWikinameTrueWithNode() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                WikiNodeDAO dao = (WikiNodeDAO)getInstance("wikiNodeDAO");
                WikiDocument newDoc = new WikiDocument();
                newDoc.setWikiname("Foobar");
                Boolean result = dao.isUniqueWikiname(3l, newDoc);
                assert result;
            }
        }.run();
    }

    @Test
    public void isUniqueWikinameFalseWithNode() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                WikiNodeDAO dao = (WikiNodeDAO)getInstance("wikiNodeDAO");
                WikiDocument newDoc = new WikiDocument();
                newDoc.setWikiname("One");
                Boolean result = dao.isUniqueWikiname(3l, newDoc);
                assert !result;
            }
        }.run();
    }

    @Test
    public void findCommentThreads() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                WikiNodeDAO dao = (WikiNodeDAO)getInstance("wikiNodeDAO");
                WikiDocument d = dao.findWikiDocument(6l);
                assert d.getName().equals("One");

                List<WikiComment> comments = dao.findWikiCommentsThreaded(d);
                assert comments.size() == 6;

                assert comments.get(0).getLevel().equals(1l);
                assert comments.get(0).getId().equals(10l);
                assert comments.get(1).getLevel().equals(2l);
                assert comments.get(1).getId().equals(11l);
                assert comments.get(2).getLevel().equals(2l);
                assert comments.get(2).getId().equals(12l);
                assert comments.get(3).getLevel().equals(3l);
                assert comments.get(3).getId().equals(13l);
                assert comments.get(4).getLevel().equals(1l);
                assert comments.get(4).getId().equals(14l);
                assert comments.get(5).getLevel().equals(2l);
                assert comments.get(5).getId().equals(15l);
            }
        }.run();
    }

    @Test
    public void findSiblings() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                WikiNodeDAO dao = (WikiNodeDAO)getInstance("wikiNodeDAO");
                WikiDocument d = dao.findWikiDocument(6l);
                assert d.getName().equals("One");

                assert dao.findSiblingWikiDocumentInDirectory(d, "createdOn", true) == null;
                assert dao.findSiblingWikiDocumentInDirectory(d, "createdOn", false).getId().equals(7l);
            }
        }.run();
    }

}
