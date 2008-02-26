package org.jboss.seam.web;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

@Scope(APPLICATION)
@Name("org.jboss.seam.web.rewriteFilter")
@Install(precedence = BUILT_IN, classDependencies="javax.faces.context.FacesContext")
@BypassInterceptors
@Filter(around="org.jboss.seam.web.HotDeployFilter")
public class RewriteFilter 
    extends AbstractFilter
{
    private static LogProvider log = Logging.getLogProvider(RewriteFilter.class);

    Collection<Pattern> patterns = null; 

    // need to extract this from Pages!
    public void setPatterns(Map<String,String> patternMap) {        
        patterns = new TreeSet<Pattern>(new Comparator<Pattern>() {
            public int compare(Pattern p1, Pattern p2) {
                return p2.pattern.compareTo(p1.pattern);
            }
        });
                
        for(Entry<String, String> entry: patternMap.entrySet()) {
            patterns.add(new Pattern(entry.getValue(), entry.getKey()));
        }
        
        log.info("Rewrite patterns: " + patterns);
    }

    public void doFilter(ServletRequest request, 
                         ServletResponse response, 
                         FilterChain chain) 
        throws IOException, 
               ServletException 
    {
        if (patterns != null) {
            if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
                response = new RewritingResponse((HttpServletRequest) request,
                        (HttpServletResponse)response,
                        patterns);
                process((HttpServletRequest) request, (HttpServletResponse) response);
            }
        }
        
        if (!response.isCommitted()) {
            chain.doFilter(request, response);
        }
    }
    
    
    @SuppressWarnings("unchecked")
    public void process(HttpServletRequest request, 
                        HttpServletResponse response)
        throws IOException, 
               ServletException 
    {
        String fullPath = request.getRequestURI();
        log.info("incoming URL is " + fullPath);

        String localPath = strip(fullPath, request.getContextPath());
       
        Rewrite rewrite = matchPatterns(localPath);
        if (rewrite!=null) {
            String newPath = rewrite.rewrite();
            
            log.info("rewritten incoming path is " + localPath);
            
            if (!fullPath.equals(request.getContextPath() + newPath)) {
                RequestDispatcher dispatcher = request.getRequestDispatcher(newPath);
                dispatcher.forward(request, response);
            }
        }
    }


    private Rewrite matchPatterns(String localPath) {
        for (Pattern pattern: patterns) {
            Rewrite rewrite = pattern.matchIncoming(localPath);
            if (rewrite!=null && rewrite.isMatch()) {
                return rewrite;
            }
        }
        return null;
    }

    private String strip(String fullPath, String contextPath) {
        if (fullPath.startsWith(contextPath)) {
            return fullPath.substring(contextPath.length());
        } else {
            return fullPath;
        }
    }
}

