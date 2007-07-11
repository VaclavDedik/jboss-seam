package org.jboss.seam.wiki.test.documents;

import org.jboss.seam.Component;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;
import org.dbunit.operation.DatabaseOperation;

public class DocumentDisplay extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.xml")
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/documents/TestAreaAndDocument.xml", DatabaseOperation.INSERT)
        );
    }

    @Test
    public void resolveNodeId() throws Exception {

        new FacesRequest() {
            protected void invokeApplication() throws Exception {
                Component.getInstance("restrictedEntityManager");
            }
        }.run();

        new NonFacesRequest("/docDisplay.xhtml") {

            protected void beforeRequest() {
                setParameter("nodeId", "101");
            }

            protected void renderResponse() throws Exception {

                Document doc = (Document)getValue("#{currentDocument}");
                assert doc != null;
                assert doc.getId().equals(101l);

            }
        }.run();
    }
}
