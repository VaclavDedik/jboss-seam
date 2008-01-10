/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.plugin;

import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.plugin.forum.ForumHome;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

public class ForumHomeTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/ForumData.dbunit.xml", DatabaseOperation.INSERT)
        );
    }

    @Test
    public void addForum() throws Exception {

        loginAdmin();

        final String conversationId = new FacesRequest() {

            protected void updateModelValues() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory forumDir = nodeDAO.findWikiDirectory(100l);
                Contexts.getPageContext().set("currentDirectory", forumDir);
            }

            protected void invokeApplication() throws Exception {
                ForumHome home = (ForumHome)getInstance(ForumHome.class);
                home.newForum();
            }
        }.run();

        new FacesRequest() {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void updateModelValues() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory forumDir = nodeDAO.findWikiDirectory(100l);
                Contexts.getPageContext().set("currentDirectory", forumDir);
            }

            protected void invokeApplication() throws Exception {
                ForumHome home = (ForumHome)getInstance(ForumHome.class);

                home.getInstance().setName("New Forum");
                home.getInstance().setDescription("This is a new forum");

                assert invokeMethod("#{forumHome.persist}") == null;
            }

            protected void renderResponse() throws Exception {
                Long newId = (Long)getValue("#{forumHome.instance.id}");

                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory newForum = nodeDAO.findWikiDirectory(newId);

                assert newForum.getAreaNumber().equals(100l);

                assert newForum.getFeed() != null;

                Session s = getHibernateSession();
                WikiMenuItem newMenuItem = (WikiMenuItem)s
                        .createQuery("select m from WikiMenuItem m where m.directory.id = :dir")
                        .setParameter("dir", newId)
                        .uniqueResult();
                assert newMenuItem.getDisplayPosition() == 2l;
                s.close();

                assert newForum.getDefaultFile().getName().equals("New Forum Forum");
                assert newForum.getDefaultFile().getAreaNumber().equals(100l);
                assert newForum.getDefaultFile().getWikiname().equals("NewForumForum");
                assert ((WikiDocument)newForum.getDefaultFile()).isNameAsTitle();
                assert newForum.getDefaultFile().getReadAccessLevel() == 0;
                assert newForum.getDefaultFile().getWriteAccessLevel() == Role.ADMINROLE_ACCESSLEVEL;
                assert newForum.getDefaultFile().getCreatedBy().getUsername().equals(User.ADMIN_USERNAME);
                assert !((WikiDocument)newForum.getDefaultFile()).isEnableCommentForm();
                assert !((WikiDocument)newForum.getDefaultFile()).isEnableComments();
                assert !((WikiDocument)newForum.getDefaultFile()).isEnableCommentsOnFeeds();
                assert ((WikiDocument)newForum.getDefaultFile()).getHeaderMacrosString()
                        .equals("clearBackground hideControls hideComments hideTags hideCreatorHistory");
                assert ((WikiDocument)newForum.getDefaultFile()).getContentMacrosString().equals("forumTopics");
                assert ((WikiDocument)newForum.getDefaultFile()).getFooterMacrosString().equals("");

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

    private Session getHibernateSession() throws Exception {
        org.jboss.ejb3.entity.InjectedEntityManagerFactory jbossEMF =
                (org.jboss.ejb3.entity.InjectedEntityManagerFactory) getInitialContext().lookup("java:/entityManagerFactories/wiki");
        return ((HibernateEntityManagerFactory) jbossEMF.getDelegate()).getSessionFactory().openSession();
    }


}