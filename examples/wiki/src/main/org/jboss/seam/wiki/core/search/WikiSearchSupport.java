package org.jboss.seam.wiki.core.search;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.model.Comment;
import org.jboss.seam.wiki.core.search.metamodel.SearchSupport;
import org.jboss.seam.wiki.core.search.metamodel.SearchableEntityHandler;
import org.jboss.seam.wiki.util.WikiUtil;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.*;

import java.util.Set;
import java.util.HashSet;

/**
 * Handlers for searchable entities of the core domain model.
 *
 * @author Christian Bauer
 */
@Name("wikiSearchSupport")
public class WikiSearchSupport extends SearchSupport {

    public Set<SearchableEntityHandler> getSearchableEntityHandlers() {

        return new HashSet<SearchableEntityHandler>() {{

            add(
                new SearchableEntityHandler<Document>() {

                    public boolean isReadAccessChecked() {
                        return true;
                    }

                    public SearchHit extractHit(Query query, Document doc) throws Exception {
                        return new SearchHit(
                            Document.class.getSimpleName(),
                            "icon.doc.gif",
                            escapeBestFragments(query, new NullFragmenter(), doc.getName(), 0, 0),
                            WikiUtil.renderURL(doc),
                            escapeBestFragments(query, new SimpleFragmenter(100), doc.getContent(), 5, 350)
                        );
                    }
                }
            );

            add(
                new SearchableEntityHandler<Comment>() {
                    public SearchHit extractHit(Query query, Comment comment) throws Exception {
                        return new SearchHit(
                            Comment.class.getSimpleName(),
                            "icon.user.gif",
                            "(" + comment.getFromUserName() + ") "
                                + escapeBestFragments(query, new NullFragmenter(), comment.getSubject(), 0, 0),
                            WikiUtil.renderURL(comment.getDocument())+ "#commentsDisplay",
                            escapeBestFragments(query, new SimpleFragmenter(100), comment.getText(), 5, 350)
                        );
                    }
                }
            );

        }};
    }

}
