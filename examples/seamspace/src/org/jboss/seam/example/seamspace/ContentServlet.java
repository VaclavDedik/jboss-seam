package org.jboss.seam.example.seamspace;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.Component;

public class ContentServlet extends HttpServlet
{
   private static final long serialVersionUID = -8461940507242022217L;

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
       throws ServletException, IOException
   {
      ImageLocal local = (ImageLocal) Component.getInstance(ImageAction.class);
      
      MemberImage img = local.getImage(Integer.parseInt(request.getParameter("id")));
      
      if (img != null)
      {
        response.setContentType(img.getContentType());
        response.getOutputStream().write(img.getData());
        response.getOutputStream().flush();
      }
   }
}
