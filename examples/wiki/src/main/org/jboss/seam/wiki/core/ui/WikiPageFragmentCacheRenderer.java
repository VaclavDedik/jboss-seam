/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.ui.component.UICache;
import org.jboss.seam.ui.util.cdk.RendererBase;
import org.jboss.seam.wiki.core.cache.PageFragmentCache;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Serializable;

/**
 * Implementation of <tt>&lt;s:cache&gt;</tt> renderer based on EHCache.
 *
 * @author Christian Bauer
 */
public class WikiPageFragmentCacheRenderer extends RendererBase {

    private static final LogProvider log = Logging.getLogProvider(UICache.class);

    @Override
    protected Class getComponentClass() {
        return UICache.class;
    }

    @Override
    protected void doEncodeChildren(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException {
        UICache cache = (UICache) component;
        if (cache.isEnabled()) {
            String key = cache.getKey();
            String cachedContent = getFromCache(key);
            if (cachedContent == null) {
                log.debug("rendering from scratch: " + key);
                StringWriter stringWriter = new StringWriter();
                ResponseWriter cachingResponseWriter = writer.cloneWithWriter(stringWriter);
                context.setResponseWriter(cachingResponseWriter);
                renderChildren(context, component);
                context.setResponseWriter(writer);
                String output = stringWriter.getBuffer().toString();
                writer.write(output);
                putInCache(key, output);
            } else {
                log.debug("rendering from cache: " + key);
                writer.write(cachedContent);
            }
        } else {
            log.debug("cached rendering is disabled for: " + cache.getKey());
            renderChildren(context, component);
        }
    }


    @Override
    public boolean getRendersChildren() {
        return true;
    }

    public static void putInCache(Serializable key, String content) {
        PageFragmentCache.instance().put(key, content);
    }

    public static String getFromCache(Serializable key) {
        return PageFragmentCache.instance().get(key);
    }

}
