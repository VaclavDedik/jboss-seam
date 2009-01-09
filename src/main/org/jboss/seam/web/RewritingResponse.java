package org.jboss.seam.web;

import java.util.Collection;

import java.net.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

public class RewritingResponse 
    extends HttpServletResponseWrapper
{
    private static LogProvider log = Logging.getLogProvider(RewritingResponse.class);

    private HttpServletRequest request;
    private Collection<Pattern> patterns;


    public RewritingResponse(HttpServletRequest request, 
                             HttpServletResponse response, 
                             Collection<Pattern> patterns) 
    {
        super(response);

        this.request  = request;
        this.patterns = patterns;   
    }   
    
    @Override
    public String encodeUrl(String url) {
        return encodeURL(url);
    }
    
    @Override
    public String encodeRedirectUrl(String url) {
        return encodeRedirectURL(url);
    }

    
    @Override
    public String encodeURL(String url) {        
        String result = encode(url);
        log.debug("encodeURL " + url + " -> " + result);
        return result;
    }
    
    @Override
    public String encodeRedirectURL(String url) {
        log.debug("encode redirectURL called with " + url);
        return encodeURL(url);
    }
    

    public String rewritePath(String path) {
        String contextPath = request.getContextPath();
                
        if (path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        for (Pattern pattern: patterns) {
            Rewrite rewrite = pattern.matchOutgoing(path);
            if (rewrite != null) {
                return request.getContextPath() + rewrite.rewrite();
            }
        }
        
        return path;
    }

    public boolean isLocalURL(URL url) {
	return url.getHost().equals(request.getServerName());
    }

    public String encode(String originalUrl) {
	if (originalUrl.startsWith("http://") || originalUrl.startsWith("https://")) {
	    try {
		URL url = new URL(originalUrl);
	
		if (isLocalURL(url)) {
		    URL newUrl = new URL(url, rewritePath(url.getFile()));
		    return newUrl.toExternalForm(); 
		}
	    } catch (MalformedURLException e) {
		// ignore - we simply don't care.  we could log this at info/debug level.
	    }
	}

	return rewritePath(originalUrl);
    }

}
