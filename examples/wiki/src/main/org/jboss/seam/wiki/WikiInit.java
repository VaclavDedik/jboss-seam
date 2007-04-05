package org.jboss.seam.wiki;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;

@Name("wikiInit")
@Scope(ScopeType.APPLICATION)
@Startup
public class WikiInit {

    @Logger static Log log;

    @Create
    public void init() {
        log.info("Starting LaceWiki...");
    }
}
