package org.jboss.seam.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * A servlet filter that lets you set the character encoding of 
 * submitted data. There are two init parameters: "encoding" and
 * "overrideClient".
 * 
 * @author Gavin King
 * 
 */
public class SeamCharacterEncodingFilter implements Filter
{

   private String encoding;
   private boolean overrideClient;

   public void destroy() {}

   public void init(FilterConfig config) throws ServletException 
   {
      encoding = config.getInitParameter("encoding");
      overrideClient = "true".equals( config.getInitParameter("overrideClient") );
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
         throws ServletException, IOException
   {
      if ( overrideClient || request.getCharacterEncoding() == null )
      {
         request.setCharacterEncoding(encoding);
      }
      filterChain.doFilter(request, response);
   }

}