/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import java.io.Serializable;

/**
 * Override Seam built-in pojoCache component with EHCache implementation.
 *
 * @author Christian Bauer
 */

@BypassInterceptors
public class PageFragmentCache {

    private static final LogProvider log = Logging.getLogProvider(PageFragmentCache.class);

    public static final String CACHE_REGION_NAME = "WikiPageFragmentCache";

    // This is threadsafe
    Cache cache;

    @Create
    public void start() throws Exception {

        log.info("starting wiki page fragment cache region");
        try {
            CacheManager manager = EHCacheManager.instance();

            cache = EHCacheManager.instance().getCache(CACHE_REGION_NAME);
            if (cache == null) {
                log.warn("Could not find configuration [" + CACHE_REGION_NAME + "]; using defaults.");
                manager.addCache(CACHE_REGION_NAME);
                cache = manager.getCache(CACHE_REGION_NAME);
                log.debug("started EHCache region: " + CACHE_REGION_NAME);
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void put(Serializable key, String content) {
        cache.put(new Element(key, content));
    }

    public String get(Serializable key) {
        Element result = cache.get(key);
        return result != null ? (String)result.getValue() : null;
    }

    public void remove(Serializable key) {
        cache.remove(key);
    }

    public static PageFragmentCache instance() {
        if (!Contexts.isApplicationContextActive()) {
            throw new IllegalStateException("No active application scope");
        }
        return (PageFragmentCache) Component.getInstance(PageFragmentCache.class, ScopeType.APPLICATION);
    }

}
