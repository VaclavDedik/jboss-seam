/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.browse;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.action.Help;
import org.jboss.seam.wiki.core.nestedset.query.NestedSetNodeWrapper;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

public class DisplayHelp extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/HelpDocuments.dbunit.xml", DatabaseOperation.INSERT)
        );
    }

    @Test
    public void checkHelpDirectories() throws Exception {

        new FacesRequest("/wiki.xhtml") {
            protected void renderResponse() throws Exception {

                Help help = (Help)getValue("#{help}");
                NestedSetNodeWrapper<WikiDirectory> root = help.getRoot();

                assert root.getWrappedChildren().size() == 4;
                assert root.getWrappedChildren().size() == root.getWrappedChildrenSorted().size();

                // These guys are sorted because we flattened the tree (see NestedSetResultTransformer docs)
                assert root.getWrappedChildren().get(0).getLevel().equals(1l);
                assert root.getWrappedChildren().get(0).getWrappedNode().getId().equals(23l);
                assert root.getWrappedChildren().get(1).getLevel().equals(2l);
                assert root.getWrappedChildren().get(1).getWrappedNode().getId().equals(25l);
                assert root.getWrappedChildren().get(2).getLevel().equals(2l);
                assert root.getWrappedChildren().get(2).getWrappedNode().getId().equals(24l);
                assert root.getWrappedChildren().get(3).getLevel().equals(1l);
                assert root.getWrappedChildren().get(3).getWrappedNode().getId().equals(21l);

                assert help.getSelectedDirectory() == null;
                assert help.getSelectedDocument() == null;

            }
        }.run();
    }

    @Test
    public void selectHelpDocument() throws Exception {

        new FacesRequest("/wiki.xhtml") {

            protected void invokeApplication() throws Exception {
                assert invokeMethod("#{help.selectDocumentByName('Wiki Text Markup')}") == null;
            }

            protected void renderResponse() throws Exception {

                Help help = (Help)getValue("#{help}");
                NestedSetNodeWrapper<WikiDirectory> root = help.getRoot();

                assert root.getWrappedChildren().size() == 4;
                assert root.getWrappedChildren().size() == root.getWrappedChildrenSorted().size();

                // These guys are sorted because we flattened the tree (see NestedSetResultTransformer docs)
                assert root.getWrappedChildren().get(0).getLevel().equals(1l);
                assert root.getWrappedChildren().get(0).getWrappedNode().getId().equals(23l);
                assert root.getWrappedChildren().get(1).getLevel().equals(2l);
                assert root.getWrappedChildren().get(1).getWrappedNode().getId().equals(25l);
                assert root.getWrappedChildren().get(2).getLevel().equals(2l);
                assert root.getWrappedChildren().get(2).getWrappedNode().getId().equals(24l);
                assert root.getWrappedChildren().get(3).getLevel().equals(1l);
                assert root.getWrappedChildren().get(3).getWrappedNode().getId().equals(21l);


                assert help.getSelectedNode().getWrappedNode().getId().equals(21l);
                assert help.getSelectedDocument().getId().equals(22l);
                assert help.getSelectedDirectory().getId().equals(21l);

            }
        }.run();
    }

}
