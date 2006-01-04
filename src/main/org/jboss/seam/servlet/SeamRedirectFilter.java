package org.jboss.seam.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class SeamRedirectFilter implements Filter {

   public void init(FilterConfig config) throws ServletException {}

   public void doFilter(ServletRequest request, ServletResponse response,
         FilterChain chain) throws IOException, ServletException {
      chain.doFilter( request, ResponseInvocationHandler.proxyResponse(response) );
   }

   public void destroy() {}

}
