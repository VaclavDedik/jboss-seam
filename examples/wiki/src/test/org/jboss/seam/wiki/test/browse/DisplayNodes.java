/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.browse;

import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.action.DirectoryHome;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.jboss.seam.log.Logging;
import org.jboss.seam.log.Log;
import org.testng.annotations.Test;
import org.dbunit.operation.DatabaseOperation;

import java.util.List;

public class DisplayNodes extends DBUnitSeamTest {

    private Log log = Logging.getLog(DisplayNodes.class);

    private static final Long TEST_DIRECTORY1_ID    = 100l;
    private static final Long TEST_DOCUMENT1_ID     = 101l;
    private static final Long TEST_DIRECTORY2_ID    = 102l;

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.xml")
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/browse/TestAreaAndDocument.xml", DatabaseOperation.INSERT)
        );
    }

    @Test
    public void documentFromNodeId() throws Exception {

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("nodeId", TEST_DOCUMENT1_ID.toString());
            }

            protected void renderResponse() throws Exception {
                Document doc = (Document)getValue("#{currentDocument}");
                assert doc != null;
                assert doc.getId().equals(TEST_DOCUMENT1_ID);

                Directory dir = (Directory)getValue("#{currentDirectory}");
                assert dir != null;
                assert dir.getId().equals(TEST_DIRECTORY1_ID);

                List<Node> currentDirectoryPath = (List<Node>)getValue("#{breadcrumb}");
                assert currentDirectoryPath.size() == 2;
                assert currentDirectoryPath.get(0).getId().equals( ((Directory)getValue("#{currentDirectory}")).getId() );
                assert currentDirectoryPath.get(1).getId().equals( ((Document)getValue("#{currentDocument}")).getId()  );

                assert getRenderedViewId().equals("/docDisplay.xhtml");
            }
            
        }.run();
    }

    @Test
    public void directoryFromNodeId() throws Exception {

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("nodeId", TEST_DIRECTORY2_ID.toString());
            }

            protected void renderResponse() throws Exception {
                Document doc = (Document)getValue("#{currentDocument}");
                assert doc == null;

                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");
                Directory dir = dirHome.getInstance();
                assert dir != null;
                assert dir.getId().equals(TEST_DIRECTORY2_ID);
                assert getRenderedViewId().equals("/dirDisplay.xhtml");
            }
        }.run();
    }

    @Test
    public void documentFromWikiName() throws Exception {

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("areaName", "TestArea1");
                setParameter("nodeName", "TestDocument1");
            }

            protected void renderResponse() throws Exception {
                Document doc = (Document)getValue("#{currentDocument}");
                assert doc != null;
                assert doc.getId().equals(TEST_DOCUMENT1_ID);

                Directory dir = (Directory)getValue("#{currentDirectory}");
                assert dir != null;
                assert dir.getId().equals(TEST_DIRECTORY1_ID);

                assert getRenderedViewId().equals("/docDisplay.xhtml");
            }
        }.run();
    }

    @Test
    public void defaultDocumentFromAreaWikiName() throws Exception {

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("areaName", "TestArea1");
            }

            protected void renderResponse() throws Exception {
                Document doc = (Document)getValue("#{currentDocument}");
                assert doc != null;
                assert doc.getId().equals(TEST_DOCUMENT1_ID);

                Directory dir = (Directory)getValue("#{currentDirectory}");
                assert dir != null;
                assert dir.getId().equals(TEST_DIRECTORY1_ID);

                assert getRenderedViewId().equals("/docDisplay.xhtml");
            }
        }.run();
    }

    @Test
    public void directoryFromAreaWikiName() throws Exception {

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("areaName", "TestArea2");
            }

            protected void renderResponse() throws Exception {
                Document doc = (Document)getValue("#{currentDocument}");
                assert doc == null;

                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");
                Directory dir = dirHome.getInstance();
                assert dir != null;
                assert dir.getId().equals(TEST_DIRECTORY2_ID);

                assert getRenderedViewId().equals("/dirDisplay.xhtml");
            }
        }.run();
    }

}
