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
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.jboss.seam.log.Logging;
import org.jboss.seam.log.Log;
import org.testng.annotations.Test;
import org.dbunit.operation.DatabaseOperation;

import java.util.List;

public class DisplayNodes extends DBUnitSeamTest {

    private Log log = Logging.getLog(DisplayNodes.class);

    private static final Long TEST_DIRECTORY_ID = new Long(100);
    private static final Long TEST_DOCUMENT_ID = new Long(101);

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.xml")
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/browse/TestAreaAndDocument.xml", DatabaseOperation.INSERT)
        );
    }

    @Test
    public void displayDocument() throws Exception {

        log.info("############################################# TEST: displayDocument()");

        new NonFacesRequest("/display.xhtml") {

            protected void beforeRequest() {
                setParameter("nodeId", TEST_DOCUMENT_ID.toString());
            }

            protected void renderResponse() throws Exception {
                Document doc = (Document)getValue("#{currentDocument}");
                assert doc != null;
                assert doc.getId().equals(TEST_DOCUMENT_ID);

                Directory dir = (Directory)getValue("#{currentDirectory}");
                assert dir != null;
                assert dir.getId().equals(TEST_DIRECTORY_ID);

                List<Node> currentDirectoryPath = (List<Node>)getValue("#{currentDirectoryPath}");
                assert currentDirectoryPath.size() == 2;
                assert currentDirectoryPath.get(0).getId().equals( ((Node)getValue("#{wikiRoot}")).getId()  );
                assert currentDirectoryPath.get(1).getId().equals( ((Directory)getValue("#{currentDirectory}")).getId() );
            }
            
        }.run();

        /* TODO: Seam filter does not run for unit tests
        new NonFacesRequest("/TestArea/TestDocument") {


            
            protected void renderResponse() throws Exception {
                Document doc = (Document)getValue("#{currentDocument}");
                assert doc != null;
                assert doc.getId().equals(TEST_DOCUMENT_ID);

                Directory dir = (Directory)getValue("#{currentDirectory}");
                assert dir != null;
                assert dir.getId().equals(TEST_DIRECTORY_ID);
            }
        }.run();
        */
    }

    @Test
    public void displayDirectory() throws Exception {

        log.info("############################################# TEST: displayDirectory()");

        new NonFacesRequest("/display.xhtml") {

            protected void beforeRequest() {
                setParameter("nodeId", TEST_DIRECTORY_ID.toString());
            }

            protected void renderResponse() throws Exception {
                Directory dir = (Directory)getValue("#{currentDirectory}");
                assert dir != null;
                assert dir.getId().equals(TEST_DIRECTORY_ID);
            }
        }.run();
    }



}
