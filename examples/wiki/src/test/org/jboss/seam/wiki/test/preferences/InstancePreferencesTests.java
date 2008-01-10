/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.preferences;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.action.PreferenceEditor;
import org.jboss.seam.wiki.core.action.DocumentHome;
import org.jboss.seam.wiki.core.action.prefs.DocumentEditorPreferences;
import org.jboss.seam.wiki.core.engine.WikiTextParser;
import org.jboss.seam.wiki.core.engine.NullWikiTextRenderer;
import org.jboss.seam.wiki.core.engine.WikiMacro;
import org.jboss.seam.wiki.core.ui.UIWikiFormattedText;
import org.jboss.seam.wiki.preferences.Preferences;
import org.jboss.seam.wiki.preferences.PreferenceValue;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceRegistry;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.jboss.seam.wiki.plugin.lastmodified.LastModifiedDocumentsPreferences;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.contexts.Contexts;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Christian Bauer
 */
public class InstancePreferencesTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void readInstancePreferences() throws Exception {

        new NonFacesRequest("/docDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("documentId", "6");
            }

            protected void renderResponse() throws Exception {

                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                assert docHome.getInstance().getId().equals(6l); // Init!

                WikiTextParser parser = new WikiTextParser(docHome.getInstance().getContent(), true, false);
                parser.setRenderer(new NullWikiTextRenderer() {
                    public String renderMacro(WikiMacro macro) {
                        Contexts.getEventContext().set(UIWikiFormattedText.CURRENT_MACRO_EVENT_VARIABLE, macro);

                        if (macro.getName().equals("lastModifiedDocuments")) {
                            LastModifiedDocumentsPreferences lmdPrefs =
                                    (LastModifiedDocumentsPreferences)Preferences.getInstance("LastModifiedDocuments", "currentMacro");
                            assert lmdPrefs.getDocumentTitleLength().equals(10l);
                        }

                        return null;
                    }
                }).parse();
            }

        }.run();
    }

    @Test
    public void updateInstancePreferences() throws Exception {

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

                docHome.setFormContent("[<=lastModifiedDocuments[documentTitleLength=66]]");

                assert invokeMethod("#{documentHome.update}").equals("updated");
            }

        }.run();


        new NonFacesRequest("/docDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("documentId", "6");
            }

            protected void renderResponse() throws Exception {

                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                assert docHome.getInstance().getId().equals(6l); // Init!

                WikiTextParser parser = new WikiTextParser(docHome.getInstance().getContent(), true, false);
                parser.setRenderer(new NullWikiTextRenderer() {
                    public String renderMacro(WikiMacro macro) {
                        Contexts.getEventContext().set(UIWikiFormattedText.CURRENT_MACRO_EVENT_VARIABLE, macro);

                        if (macro.getName().equals("lastModifiedDocuments")) {
                            LastModifiedDocumentsPreferences lmdPrefs =
                                    (LastModifiedDocumentsPreferences)Preferences.getInstance("LastModifiedDocuments", "currentMacro");
                            assert lmdPrefs.getDocumentTitleLength().equals(66l);
                        }

                        return null;
                    }
                }).parse();
            }

        }.run();
    }

}