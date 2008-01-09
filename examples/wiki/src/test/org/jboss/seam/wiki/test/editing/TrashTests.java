/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.editing;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.jboss.seam.wiki.core.action.DocumentHome;
import org.jboss.seam.wiki.core.action.DirectoryHome;
import org.jboss.seam.wiki.core.action.UploadHome;
import org.jboss.seam.wiki.core.action.PreferenceEditor;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiFeed;
import org.jboss.seam.wiki.core.model.FeedEntry;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.feeds.FeedDAO;
import org.jboss.seam.wiki.preferences.PreferenceProvider;
import org.jboss.seam.wiki.preferences.PreferenceValue;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceRegistry;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Christian Bauer
 */
public class TrashTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/UploadData.dbunit.xml", DatabaseOperation.INSERT)
        );
    }

    @Test
    public void trashDeleteDocument2() throws Exception {

        // Set the wikiStart to something else so we can delete this document
        loginAdmin();

        final String prefsConversationId = new NonFacesRequest("/adminHome_d.xhtml") {}.run();

        new FacesRequest("/adminHome_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", prefsConversationId);
            }

            protected void invokeApplication() throws Exception {

                PreferenceRegistry registry = (PreferenceRegistry)getInstance("preferenceRegistry");
                PreferenceEntity wikiEntity = registry.getPreferenceEntitiesByName().get("Wiki");

                invokeMethod("#{adminHome.initPreferencesEditor}");

                PreferenceEditor prefEditor = (PreferenceEditor)getInstance("preferenceEditor");
                prefEditor.selectPreferenceEntity(wikiEntity);
            }

        }.run();

        new FacesRequest("/adminHome_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", prefsConversationId);
            }

            protected void invokeApplication() throws Exception {
                PreferenceEditor prefEditor = (PreferenceEditor)getInstance("preferenceEditor");
                List<PreferenceValue> values = prefEditor.getPreferenceValues();
                // This is somewhat dodgy... no other way to get the value we want
                for (PreferenceValue value : values) {
                    if (value.getPreferenceProperty().getFieldName().equals("defaultDocumentId")) {
                        value.setValue(7l);
                    }
                }
                assert invokeMethod("#{adminHome.update()}") == null;
            }

        }.run();

        // Now delete this document
        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("documentId", "6");
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                DocumentHome docHome = (DocumentHome)getInstance("documentHome");
                assert docHome.getInstance().getId().equals(6l); // Init!

                assert invokeMethod("#{documentHome.remove}").equals("removed");
            }
        }.run();

        loginAdmin();

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("areaName", "Trash");
            }

            protected void renderResponse() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");
                assert dirHome.getInstance().getId().equals(17l); // Init!

                assert dirHome.getChildNodes().size() == 1;
                assert dirHome.getChildNodes().get(0).getId().equals(6l);

                assert getRenderedViewId().equals("/dirDisplay_d.xhtml");
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "17");
            }

            protected void invokeApplication() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");
                assert dirHome.getInstance().getId().equals(17l); // Init!
                dirHome.emptyTrash();
            }
        }.run();

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("areaName", "Trash");
            }

            protected void renderResponse() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");
                assert dirHome.getInstance().getId().equals(17l); // Init!

                assert dirHome.getChildNodes().size() == 0;

                WikiNodeDAO dao = (WikiNodeDAO)getInstance("wikiNodeDAO");
                assert dao.findWikiNode(6l) == null;

                assert dao.findWikiComment(10l) == null;
                assert dao.findWikiComment(11l) == null;
                assert dao.findWikiComment(12l) == null;
                assert dao.findWikiComment(13l) == null;
                assert dao.findWikiComment(14l) == null;
                assert dao.findWikiComment(15l) == null;

                assert getRenderedViewId().equals("/dirDisplay_d.xhtml");

                // Comments should be gone
            }
        }.run();
    }

    @Test
    public void trashDeleteDocument() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("documentId", "9");
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                DocumentHome docHome = (DocumentHome)getInstance("documentHome");
                assert docHome.getInstance().getId().equals(9l); // Init!

                assert invokeMethod("#{documentHome.remove}").equals("removed");
            }

            // Feed entries should be gone
            protected void renderResponse() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance("wikiNodeDAO");
                WikiDocument document = nodeDAO.findWikiDocument(9l);

                FeedDAO feedDAO = (FeedDAO)getInstance("feedDAO");

                List<WikiFeed> feeds = feedDAO.findFeeds(document);
                assert feeds.size() == 0;
            }
        }.run();

        loginAdmin();

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("areaName", "Trash");
            }

            protected void renderResponse() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");
                assert dirHome.getInstance().getId().equals(17l); // Init!

                assert dirHome.getChildNodes().size() == 1;
                assert dirHome.getChildNodes().get(0).getId().equals(9l);

                assert getRenderedViewId().equals("/dirDisplay_d.xhtml");
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "17");
            }

            protected void invokeApplication() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");
                assert dirHome.getInstance().getId().equals(17l); // Init!
                dirHome.emptyTrash();
            }
        }.run();

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("areaName", "Trash");
            }

            protected void renderResponse() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");
                assert dirHome.getInstance().getId().equals(17l); // Init!

                assert dirHome.getChildNodes().size() == 0;

                WikiNodeDAO dao = (WikiNodeDAO)getInstance("wikiNodeDAO");
                assert dao.findWikiNode(9l) == null;

                assert getRenderedViewId().equals("/dirDisplay_d.xhtml");

                // Comments should be gone
            }
        }.run();
    }

    @Test
    public void trashDeleteUpload() throws Exception {

        final String conversationId = new NonFacesRequest("/uploadEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("uploadId", "30");
            }
        }.run();

        new FacesRequest("/uploadEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                UploadHome uploadHome = (UploadHome)getInstance("uploadHome");
                assert uploadHome.getInstance().getId().equals(30l); // Init!

                assert invokeMethod("#{uploadHome.remove}").equals("removed");
            }
        }.run();

        loginAdmin();

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("areaName", "Trash");
            }

            protected void renderResponse() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");
                assert dirHome.getInstance().getId().equals(17l); // Init!

                assert dirHome.getChildNodes().size() == 1;
                assert dirHome.getChildNodes().get(0).getId().equals(30l);

                assert getRenderedViewId().equals("/dirDisplay_d.xhtml");
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "17");
            }

            protected void invokeApplication() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");
                assert dirHome.getInstance().getId().equals(17l); // Init!
                dirHome.emptyTrash();
            }
        }.run();

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("areaName", "Trash");
            }

            protected void renderResponse() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance("directoryHome");

                assert dirHome.getChildNodes().size() == 0;

                WikiNodeDAO dao = (WikiNodeDAO)getInstance("wikiNodeDAO");
                assert dao.findWikiNode(30l) == null;

                assert getRenderedViewId().equals("/dirDisplay_d.xhtml");
            }
        }.run();
    }

    private void loginAdmin() throws Exception {
        new FacesRequest() {
           protected void invokeApplication() throws Exception {
              setValue("#{identity.username}", "admin");
              setValue("#{identity.password}", "admin");
              invokeAction("#{identity.login}");
              assert getValue("#{identity.loggedIn}").equals(true);
           }
        }.run();
    }

}
