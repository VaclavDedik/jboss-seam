package org.jboss.seam.wiki.test.documents;

import org.testng.annotations.Test;
import org.jboss.seam.mock.SeamTest;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.Component;

public class DocumentDisplay extends SeamTest {

    @Test
    public void resolveNodeId() throws Exception {

        new FacesRequest() {
            protected void invokeApplication() throws Exception {
                Component.getInstance("restrictedEntityManager");
            }
        }.run();

        new NonFacesRequest("/docDisplay.xhtml") {

            protected void beforeRequest() {
                setParameter("nodeId", "10");
            }

            protected void renderResponse() throws Exception {

                Document doc = (Document)getValue("#{currentDocument}");
                assert doc != null;
                assert doc.getId().equals(10l);

            }
        }.run();
    }
}
