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
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiUpload;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

public class Linking extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/UploadData.dbunit.xml", DatabaseOperation.INSERT)
        );
    }

    @Test
    public void linkToKnownProtocols() throws Exception {

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

                docHome.setFormContent("[=>http://foo.bar]");
                assert docHome.getInstance().getContent().equals("[=>http://foo.bar]");
                assert docHome.getFormContent().equals("[=>http://foo.bar]");

                docHome.setFormContent("[=>https://foo.bar]");
                assert docHome.getInstance().getContent().equals("[=>https://foo.bar]");
                assert docHome.getFormContent().equals("[=>https://foo.bar]");

                docHome.setFormContent("[=>ftp://foo.bar]");
                assert docHome.getInstance().getContent().equals("[=>ftp://foo.bar]");
                assert docHome.getFormContent().equals("[=>ftp://foo.bar]");

                docHome.setFormContent("[=>mailto:foo@bar.tld]");
                assert docHome.getInstance().getContent().equals("[=>mailto:foo@bar.tld]");
                assert docHome.getFormContent().equals("[=>mailto:foo@bar.tld]");

                docHome.setFormContent("[Foo Bar=>http://foo.bar]");
                assert docHome.getInstance().getContent().equals("[Foo Bar=>http://foo.bar]");
                assert docHome.getFormContent().equals("[Foo Bar=>http://foo.bar]");

                docHome.setFormContent("[Foo Bar=>https://foo.bar]");
                assert docHome.getInstance().getContent().equals("[Foo Bar=>https://foo.bar]");
                assert docHome.getFormContent().equals("[Foo Bar=>https://foo.bar]");

                docHome.setFormContent("[Foo Bar=>ftp://foo.bar]");
                assert docHome.getInstance().getContent().equals("[Foo Bar=>ftp://foo.bar]");
                assert docHome.getFormContent().equals("[Foo Bar=>ftp://foo.bar]");

                docHome.setFormContent("[Foo Bar=>mailto:foo@bar.tld]");
                assert docHome.getInstance().getContent().equals("[Foo Bar=>mailto:foo@bar.tld]");
                assert docHome.getFormContent().equals("[Foo Bar=>mailto:foo@bar.tld]");

            }

        }.run();
    }

    @Test
    public void linkToDocuments() throws Exception {

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

                docHome.setFormContent("[=>Two]");
                assert docHome.getInstance().getContent().equals("[=>wiki://7]");
                assert docHome.getFormContent().equals("[=>Two]");

                docHome.setFormContent("[=>Four]");
                assert docHome.getInstance().getContent().equals("[=>Four]");
                assert docHome.getFormContent().equals("[=>Four]");

                docHome.setFormContent("[=>BBB|Four]");
                assert docHome.getInstance().getContent().equals("[=>wiki://9]");
                assert docHome.getFormContent().equals("[=>BBB|Four]");

                docHome.setFormContent("[Foo Bar=>Two]");
                assert docHome.getInstance().getContent().equals("[Foo Bar=>wiki://7]");
                assert docHome.getFormContent().equals("[Foo Bar=>Two]");

                docHome.setFormContent("[Foo Bar=>Four]");
                assert docHome.getInstance().getContent().equals("[Foo Bar=>Four]");
                assert docHome.getFormContent().equals("[Foo Bar=>Four]");

                docHome.setFormContent("[Foo Bar=>BBB|Four]");
                assert docHome.getInstance().getContent().equals("[Foo Bar=>wiki://9]");
                assert docHome.getFormContent().equals("[Foo Bar=>BBB|Four]");

            }

        }.run();
    }

    @Test
    public void linkToUploads() throws Exception {

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

                docHome.setFormContent("[=>Two]");
                assert docHome.getInstance().getContent().equals("[=>wiki://7]");
                assert docHome.getFormContent().equals("[=>Two]");

                docHome.setFormContent("[=>BBB|Test Image]");
                assert docHome.getInstance().getContent().equals("[=>wiki://30]");
                assert docHome.getFormContent().equals("[=>BBB|Test Image]");

            }

        }.run();
    }

    @Test
    public void linkSourceTargetTracking() throws Exception {

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

                assert docHome.getInstance().getOutgoingLinks().size() == 0;
                docHome.setFormContent("[=>Two] and [=>Three] and [=>BBB|Test Image]");

                assert invokeMethod("#{documentHome.update}").equals("updated");

                assert docHome.getInstance().getContent().equals("[=>wiki://7] and [=>wiki://8] and [=>wiki://30]");
                assert docHome.getInstance().getOutgoingLinks().size() == 3;

                docHome.getEntityManager().clear();
                WikiDocument d = (WikiDocument)
                        docHome.getEntityManager().createQuery("select d from WikiDocument d left join fetch d.incomingLinks where d.id = :id")
                                .setParameter("id", 7l)
                                .getSingleResult();
                assert d.getIncomingLinks().size() == 1;
                docHome.getEntityManager().clear();
                d = (WikiDocument)
                        docHome.getEntityManager().createQuery("select d from WikiDocument d left join fetch d.incomingLinks where d.id = :id")
                                .setParameter("id", 8l)
                                .getSingleResult();
                assert d.getIncomingLinks().size() == 2;
                docHome.getEntityManager().clear();
                WikiUpload f = (WikiUpload)
                        docHome.getEntityManager().createQuery("select f from WikiUpload f left join fetch f.incomingLinks where f.id = :id")
                                .setParameter("id", 30l)
                                .getSingleResult();
                assert f.getIncomingLinks().size() == 1;
            }

        }.run();
    }


}