package org.jboss.seam.web;

import java.util.Collection;

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
        return super.encodeURL(url);
    }
    
    @Override
    public String encodeRedirectUrl(String url) {
        return encodeRedirectURL(url);
    }

    
    @Override
    public String encodeURL(String url) {        
        String result = encode(url);
        log.info("encodeURL " + url + " -> " + result);
        return result;
    }
    
    @Override
    public String encodeRedirectURL(String url) {
        log.info("encode redirectURL called with " + url);
        return super.encodeRedirectURL(url);
    }
    
    
    public String encode(String originalUrl) {
        String url = originalUrl;
        String contextPath = request.getContextPath();
                
        if (url.startsWith(contextPath)) {
            url = url.substring(contextPath.length());
        }

        for (Pattern pattern: patterns) {
            Rewrite rewrite = pattern.matchOutgoing(url);
            if (rewrite != null) {
                return request.getContextPath() + rewrite.rewrite();
            }
        }
        
        return originalUrl;
    }

}
