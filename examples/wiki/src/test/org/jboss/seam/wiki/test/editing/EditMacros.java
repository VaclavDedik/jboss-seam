/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.editing;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.wiki.core.action.DocumentHome;
import org.jboss.seam.wiki.core.engine.WikiMacro;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

public class EditMacros extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void marshallMacros() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("documentId", "6");
                setParameter("parentDirectoryId", "3");
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                assert Conversation.instance().isLongRunning();

                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                assert docHome.getInstance().getId().equals(6l); // Init!

                assert docHome.getInstance().getHeaderMacros().size() == 2;
                assert docHome.getInstance().getFooterMacros().size() == 2;

                boolean macroFound = false;
                for (WikiMacro wikiMacro : docHome.getInstance().getContentMacros()) {
                    if (wikiMacro.getName().equals("lastModifiedDocuments")) {
                        assert wikiMacro.getParams().size()==2;
                        assert wikiMacro.getParams().get("documentTitleLength").equals("10");
                        assert wikiMacro.getParams().get("showUsernames").equals("true");
                        macroFound = true;
                    }
                }
                assert macroFound;

            }

        }.run();
    }

    @Test
    public void editMacros() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("documentId", "6");
                setParameter("parentDirectoryId", "3");
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                assert Conversation.instance().isLongRunning();

                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                assert docHome.getInstance().getId().equals(6l); // Init!

                docHome.setFormContent("[<=contentMacro[param=value]]");

                assert docHome.getInstance().getHeaderMacros().size() == 2;
                assert docHome.getInstance().getContentMacros().size() == 1;
                assert docHome.getInstance().getFooterMacros().size() == 2;

                // Check WikiMacro.equals() as well
                WikiMacro macro = new WikiMacro("contentMacro");
                macro.setPosition(0);

                boolean macroFound = false;
                for (WikiMacro wikiMacro : docHome.getInstance().getContentMacros()) {
                    if (wikiMacro.equals(macro)) {
                        assert wikiMacro.getParams().size()==1;
                        assert wikiMacro.getParams().get("param").equals("value");
                        macroFound = true;
                    }
                }
                assert macroFound;
            }

        }.run();
    }

}