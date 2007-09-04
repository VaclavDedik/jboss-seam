/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.editing;

import org.dbunit.operation.DatabaseOperation;
import org.hibernate.StatelessSession;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.wiki.core.action.DirectoryHome;
import org.jboss.seam.wiki.core.action.DocumentHome;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.nestedset.NestedSetNodeWrapper;
import org.jboss.seam.wiki.core.nestedset.NestedSetResultTransformer;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.Component;
import org.testng.annotations.Test;

import java.util.Comparator;

public class BasicNodeOperations extends DBUnitSeamTest {

    private Log log = Logging.getLog(BasicNodeOperations.class);

    private static final Long TEST_WIKI_ROOT_ID = 1l;

    private static final Long TEST_DIRECTORY1_ID = 100l;
    private static final Long TEST_DOCUMENT1_ID = 101l;
    private static final Long TEST_DOCUMENT2_ID = 102l;
    private static final Long TEST_DOCUMENT3_ID = 103l;
    private static final Long TEST_DIRECTORY2_ID = 104l;
    private static final Long TEST_DOCUMENT5_ID = 105l;
    private static final Long TEST_DOCUMENT6_ID = 106l;
    private static final Long TEST_DOCUMENT7_ID = 107l;

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.xml")
        );
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/wiki/test/editing/NestedNodes.xml", DatabaseOperation.INSERT)
        );
    }

    @Test
    public void deleteDocument() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("documentId", TEST_DOCUMENT1_ID.toString());
                setParameter("parentDirectoryId", TEST_DIRECTORY1_ID.toString());
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                assert Conversation.instance().isLongRunning();

                DocumentHome docHome = (DocumentHome)getInstance("documentHome");
                assert checkNestedSetNodeInMemory( docHome.getInstance(), 9, 10);
                assert checkNestedSetNodeInMemory( docHome.getParentDirectory(), 8, 23);

                assert invokeMethod("#{documentHome.remove}").equals("removed");

                // TODO: SeamTest doesn't do navigation but we don't want to have /docEdit_d.xhtml in the RENDER RESPONSE
                Conversation.instance().end();
                Redirect.instance().setViewId("/dirDisplay.xhtml");
                Redirect.instance().execute();

                assert checkNestedSetNodeInDatabase(TEST_DIRECTORY1_ID, 8, 21);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT2_ID, 9, 10);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT3_ID, 11, 12);
                assert checkNestedSetNodeInDatabase(TEST_DIRECTORY2_ID, 13, 18);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT5_ID, 14, 15);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT6_ID, 16, 17);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT7_ID, 19, 20);
                assert checkNestedSetNodeInMemory( (Node)getValue("#{documentHome.parentDirectory}"), 8, 21);
                assert !checkNodeInDatabase(TEST_DOCUMENT1_ID);

                displayNodeTree( (NodeDAO)getValue("#{nodeDAO}"), (Node)getValue("#{documentHome.parentDirectory}") );
            }

        }.run();
    }

    @Test
    public void deleteDirectory() throws Exception {

        final String conversationId = new NonFacesRequest("/dirEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("directoryId", TEST_DIRECTORY2_ID.toString());
                setParameter("parentDirectoryId", TEST_DIRECTORY1_ID.toString());
            }
        }.run();

        new FacesRequest("/dirEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");
                dirHome.init(); // TODO: Seam test doesn't call page actions
                assert checkNestedSetNodeInDatabase(TEST_DIRECTORY1_ID, 8, 23);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT1_ID, 9, 10);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT2_ID, 11, 12);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT3_ID, 13, 14);
                assert checkNestedSetNodeInDatabase(TEST_DIRECTORY2_ID, 15, 20);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT5_ID, 16, 17);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT6_ID, 18, 19);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT7_ID, 21, 22);

                assert checkNestedSetNodeInMemory( dirHome.getInstance(), 15, 20);
                assert checkNestedSetNodeInMemory( dirHome.getParentDirectory(), 8, 23);

                assert invokeMethod("#{directoryHome.remove}").equals("removed");

                // TODO: SeamTest doesn't do navigation but we don't want to have /dirEdit_d.xhtml in the RENDER RESPONSE
                Conversation.instance().end();
                Redirect.instance().setViewId("/dirDisplay.xhtml");
                Redirect.instance().execute();

                assert checkNestedSetNodeInDatabase(TEST_DIRECTORY1_ID, 8, 17);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT1_ID, 9, 10);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT2_ID, 11, 12);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT3_ID, 13, 14);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT7_ID, 15, 16);
                assert checkNestedSetNodeInMemory( (Node)getValue("#{directoryHome.parentDirectory}"), 8, 17);
                assert !checkNodeInDatabase(TEST_DIRECTORY2_ID);
                assert !checkNodeInDatabase(TEST_DOCUMENT5_ID);
                assert !checkNodeInDatabase(TEST_DOCUMENT6_ID);

                displayNodeTree( (NodeDAO)getValue("#{nodeDAO}"), (Node)getValue("#{directoryHome.parentDirectory}") );
            }

        }.run();
    }

    @Test
    public void createDocumentInArea() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("parentDirectoryId", TEST_DIRECTORY1_ID.toString());
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {

                DocumentHome docHome = (DocumentHome)getInstance("documentHome");
                docHome.getInstance().setName("Testname");
                docHome.getInstance().setReadAccessLevel(0);
                docHome.getInstance().setWriteAccessLevel(0);
                docHome.setFormContent("Testcontent");

                assert checkNestedSetNodeInDatabase(TEST_DIRECTORY1_ID, 8, 23);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT1_ID, 9, 10);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT2_ID, 11, 12);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT3_ID, 13, 14);
                assert checkNestedSetNodeInDatabase(TEST_DIRECTORY2_ID, 15, 20);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT5_ID, 16, 17);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT6_ID, 18, 19);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT7_ID, 21, 22);

                assert checkNestedSetNodeInMemory( docHome.getParentDirectory(), 8, 23);

                assert invokeMethod("#{documentHome.persist}").equals("persisted");

            }

            protected void renderResponse() throws Exception {

                assert checkNestedSetNodeInDatabase(TEST_DIRECTORY1_ID, 8, 25);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT1_ID, 9, 10);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT2_ID, 11, 12);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT3_ID, 13, 14);
                assert checkNestedSetNodeInDatabase(TEST_DIRECTORY2_ID, 15, 20);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT5_ID, 16, 17);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT6_ID, 18, 19);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT7_ID, 21, 22);
                Node newNode = (Node)getValue("#{documentHome.instance}");
                assert checkNodeInDatabase(newNode.getId());
                assert checkNestedSetNodeInMemory(newNode, 23, 24);
                assert checkNestedSetNodeInDatabase(newNode.getId(), 23, 24);

                Node parentDir = (Node)getValue("#{documentHome.parentDirectory}");
                assert checkNestedSetNodeInMemory(parentDir, 8, 25);

                displayNodeTree( (NodeDAO)getValue("#{nodeDAO}"), parentDir );
            }

        }.run();
    }

    @Test
    public void createDocumentInSubdirectory() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("parentDirectoryId", TEST_DIRECTORY2_ID.toString());
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                DocumentHome docHome = (DocumentHome)getInstance("documentHome");

                docHome.getInstance().setName("Testname");
                docHome.getInstance().setReadAccessLevel(0);
                docHome.getInstance().setWriteAccessLevel(0);
                docHome.setFormContent("Testcontent");

                assert checkNestedSetNodeInDatabase(TEST_DIRECTORY1_ID, 8, 23);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT1_ID, 9, 10);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT2_ID, 11, 12);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT3_ID, 13, 14);
                assert checkNestedSetNodeInDatabase(TEST_DIRECTORY2_ID, 15, 20);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT5_ID, 16, 17);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT6_ID, 18, 19);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT7_ID, 21, 22);

                assert checkNestedSetNodeInMemory( docHome.getParentDirectory(), 15, 20);

                assert invokeMethod("#{documentHome.persist}").equals("persisted");
            }

            protected void renderResponse() throws Exception {
                assert checkNestedSetNodeInDatabase(TEST_DIRECTORY1_ID, 8, 25);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT1_ID, 9, 10);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT2_ID, 11, 12);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT3_ID, 13, 14);
                assert checkNestedSetNodeInDatabase(TEST_DIRECTORY2_ID, 15, 22);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT5_ID, 16, 17);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT6_ID, 18, 19);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT7_ID, 23, 24);
                Node newNode = (Node)getValue("#{documentHome.instance}");
                assert checkNodeInDatabase(newNode.getId());
                assert checkNestedSetNodeInMemory(newNode, 20, 21);
                assert checkNestedSetNodeInDatabase(newNode.getId(), 20, 21);

                Node parentDir = (Node)getValue("#{documentHome.parentDirectory}");
                assert checkNestedSetNodeInMemory(parentDir, 15, 22);

                displayNodeTree( (NodeDAO)getValue("#{nodeDAO}"), parentDir );
            }

        }.run();
    }

    @Test
    public void moveDirectoryLeft() throws Exception {

        final String conversationId = new NonFacesRequest("/dirEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("directoryId", TEST_DIRECTORY1_ID.toString());
                setParameter("parentDirectoryId", TEST_WIKI_ROOT_ID.toString());
            }
        }.run();

        new FacesRequest("/dirEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");

                // Move the TEST_DIRECTORY2 (position 3) to position 1
                WikiUtil.shiftListElement(dirHome.getInstance().getChildren(), 3, 1);

                assert invokeMethod("#{directoryHome.update}").equals("updated");
            }

            protected void renderResponse() throws Exception {

                assert checkNestedSetNodeInDatabase(TEST_DIRECTORY1_ID, 8, 23);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT1_ID, 9, 10);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT2_ID, 11, 12);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT3_ID, 13, 14);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT7_ID, 21, 22);
                assert checkNestedSetNodeInDatabase(TEST_DIRECTORY2_ID, 15, 20);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT5_ID, 16, 17);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT6_ID, 18, 19);

                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");

                assert dirHome.getInstance().getChildren().get(0).getId().equals(TEST_DOCUMENT1_ID);
                assert checkNestedSetNodeInMemory(dirHome.getInstance().getChildren().get(0), 9, 10);

                displayNodeTree( (NodeDAO)getValue("#{nodeDAO}"), (Node)getValue("#{directoryHome.instance}") );

            }

        }.run();
    }


    @Test
    public void moveDocumentRight() throws Exception {

        final String conversationId = new NonFacesRequest("/dirEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("directoryId", TEST_DIRECTORY1_ID.toString());
                setParameter("parentDirectoryId", TEST_WIKI_ROOT_ID.toString());
            }
        }.run();

        new FacesRequest("/dirEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");

                // Move the TEST_DOCUMENT2 (position 1) to position 3
                WikiUtil.shiftListElement(dirHome.getInstance().getChildren(), 1, 3);

                assert invokeMethod("#{directoryHome.update}").equals("updated");
            }

            protected void renderResponse() throws Exception {

                assert checkNestedSetNodeInDatabase(TEST_DIRECTORY1_ID, 8, 23);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT1_ID, 9, 10);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT2_ID, 11, 12);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT3_ID, 13, 14);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT7_ID, 21, 22);
                assert checkNestedSetNodeInDatabase(TEST_DIRECTORY2_ID, 15, 20);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT5_ID, 16, 17);
                assert checkNestedSetNodeInDatabase(TEST_DOCUMENT6_ID, 18, 19);

                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");

                assert dirHome.getInstance().getChildren().get(0).getId().equals(TEST_DOCUMENT1_ID);
                assert checkNestedSetNodeInMemory(dirHome.getInstance().getChildren().get(0), 9, 10);

                displayNodeTree( (NodeDAO)getValue("#{nodeDAO}"), (Node)getValue("#{directoryHome.instance}") );
            }

        }.run();
    }

    /* ############################################################################################################## */

    private boolean checkNestedSetNodeInMemory(Node node, long left, long right) throws Exception {
        return node.getNsLeft() == left && node.getNsRight() == right;
    }

    private boolean checkNestedSetNodeInDatabase(long nodeId, long left, long right) throws Exception {
        StatelessSession ss = getStatelessSession();
        Node node = (Node)ss.createQuery("select n from Node n left join fetch n.parent where n.id = :id").setParameter("id", nodeId).uniqueResult();
        ss.close();
        return node.getNsLeft() == left && node.getNsRight() == right;
    }

    private boolean checkNodeInDatabase(long nodeId) throws Exception {
        StatelessSession ss = getStatelessSession();
        Node node = (Node)ss.createQuery("select n from Node n left join fetch n.parent where n.id = :id").setParameter("id", nodeId).uniqueResult();
        ss.close();
        return node != null;
    }

    private StatelessSession getStatelessSession() throws Exception {
        org.jboss.ejb3.entity.InjectedEntityManagerFactory jbossEMF =
                (org.jboss.ejb3.entity.InjectedEntityManagerFactory) getInitialContext().lookup("java:/entityManagerFactories/wiki");
        return ((HibernateEntityManagerFactory) jbossEMF.getDelegate()).getSessionFactory().openStatelessSession();
    }

    private void displayNodeTree(NodeDAO dao, Node startNode) {
        if (log.isTraceEnabled()) {

            Comparator<NestedSetNodeWrapper<Node>> comp =
                new Comparator<NestedSetNodeWrapper<Node>>() {
                    public int compare(NestedSetNodeWrapper<Node> o, NestedSetNodeWrapper<Node> o2) {
                        return o.getWrappedNode().getDisplayPosition().compareTo(o2.getWrappedNode().getDisplayPosition());
                    }
                };
            NestedSetNodeWrapper<Node> startNodeWrapper = new NestedSetNodeWrapper<Node>(startNode, comp);
            NestedSetResultTransformer<Node> transformer = new NestedSetResultTransformer<Node>(startNodeWrapper);
            dao.appendNestedSetNodes(transformer, null, false);

            log.trace("######################################## TREE BEGIN #####################################################");
            displayNodes(startNodeWrapper);
            log.trace("######################################## TREE END   #####################################################");
        }
    }
    private void displayNodes(NestedSetNodeWrapper<Node> startNode) {
        StringBuilder levelMarkers = new StringBuilder();
        for (int i = 1; i <= startNode.getLevel(); i++) {
            levelMarkers.append("#");
        }
        log.trace(levelMarkers.toString() + " " + startNode);
        for (NestedSetNodeWrapper<Node> next : startNode.getWrappedChildren()) {
            displayNodes(next);
        }
    }

}
