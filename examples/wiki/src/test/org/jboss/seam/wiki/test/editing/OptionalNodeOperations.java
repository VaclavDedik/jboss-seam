package org.jboss.seam.wiki.test.editing;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.jboss.seam.wiki.core.action.DirectoryHome;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.core.Conversation;
import org.testng.annotations.Test;

public class OptionalNodeOperations extends DBUnitSeamTest {

    private Log log = Logging.getLog(OptionalNodeOperations.class);

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
    public void setDefaultDocument() throws Exception {

        final String conversationId = new NonFacesRequest("/dirEdit.xhtml") {
            protected void beforeRequest() {
                setParameter("directoryId", TEST_DIRECTORY1_ID.toString());
                setParameter("parentDirectoryId", TEST_WIKI_ROOT_ID.toString());
            }
        }.run();

        new FacesRequest("/dirEdit.xhtml") {

            Long newDefaultDocumentId = null;

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                assert Conversation.instance().isLongRunning();

                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");

                // Just take the first one
                Document defaultDocument = dirHome.getChildDocuments().get(0);
                dirHome.getInstance().setDefaultDocument(defaultDocument);
                newDefaultDocumentId = defaultDocument.getId();

                assert invokeMethod("#{directoryHome.update}").equals("updated");
            }

            protected void renderResponse() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");
                assert dirHome.getInstance().getDefaultDocument().getId().equals(newDefaultDocumentId);
            }
        }.run();
    }

    @Test
    public void changeDefaultDocument() throws Exception {

        final String conversationId = new NonFacesRequest("/dirEdit.xhtml") {
            protected void beforeRequest() {
                setParameter("directoryId", TEST_DIRECTORY2_ID.toString());
                setParameter("parentDirectoryId", TEST_DIRECTORY1_ID.toString());
            }
        }.run();

        new FacesRequest("/dirEdit.xhtml") {

            Long newDefaultDocumentId = null;

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                assert Conversation.instance().isLongRunning();

                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");

                // Switch from first to second
                Document defaultDocument = dirHome.getChildDocuments().get(1);
                dirHome.getInstance().setDefaultDocument(defaultDocument);
                newDefaultDocumentId = defaultDocument.getId();

                assert invokeMethod("#{directoryHome.update}").equals("updated");
            }

            protected void renderResponse() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");
                assert dirHome.getInstance().getDefaultDocument().getId().equals(newDefaultDocumentId);
            }
        }.run();
    }

}
