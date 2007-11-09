package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.wiki.core.search.metamodel.SearchSupport;
import org.jboss.seam.wiki.core.search.metamodel.SearchableEntityHandler;
import org.jboss.seam.wiki.core.search.SearchHit;
import org.jboss.seam.wiki.util.WikiUtil;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.*;

import java.util.HashSet;
import java.util.Set;

@Name("forumSearchSupport")
public class ForumSearchSupport extends SearchSupport {

    public Set<SearchableEntityHandler> getSearchableEntityHandlers() {

        return new HashSet<SearchableEntityHandler>() {{

            add(
                new SearchableEntityHandler<ForumTopic>() {

                    public boolean isReadAccessChecked() {
                        return true;
                    }

                    public SearchHit extractHit(Query query, ForumTopic forumTopic) throws Exception {
                        return new SearchHit(
                            ForumTopic.class.getSimpleName(),
                            "icon.posting.gif",
                            escapeBestFragments(query, new NullFragmenter(), forumTopic.getName(), 0, 0),
                            WikiUtil.renderURL(forumTopic),
                            escapeBestFragments(query, new SimpleFragmenter(100), forumTopic.getContent(), 5, 350)
                        );
                    }
                }
            );

        }};
    }

}
