/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.blogdirectory;

import org.jboss.seam.wiki.core.template.WikiDocumentTemplate;
import org.jboss.seam.wiki.core.template.WikiDocumentEditorTemplate;
import org.jboss.seam.wiki.core.model.WikiDocumentDefaults;
import org.jboss.seam.wiki.core.action.DocumentHome;
import org.jboss.seam.international.Messages;

/**
 * @author Christian Bauer
 */
@WikiDocumentTemplate("#{messages['blogDirectory.label.template.BlogEntry']}")
public class BlogEntryDefaults extends WikiDocumentDefaults implements WikiDocumentEditorTemplate {

    @Override
    public String getName() {
        return Messages.instance().get("blogDirectory.label.template.NewBlogEntryTitle");
    }

    @Override
    public String[] getHeaderMacrosAsString() {
        return new String[]{ "blogEntry" };
    }

    public void setEditorDefaults(DocumentHome editor) {
        editor.setPushOnFeeds(true);
        editor.setPushOnSiteFeed(true);
    }
}
