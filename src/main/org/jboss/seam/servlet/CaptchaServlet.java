package org.jboss.seam.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.security.CaptchaService;

import com.octo.captcha.service.CaptchaServiceException;

/**
 * A servlet that provides captcha images
 * 
 * @author Shane Bryzak
 */
public class CaptchaServlet extends HttpServlet 
{
   private ServletContext context;
   
   @Override
   public void init(ServletConfig config)
      throws ServletException
   {      
      super.init(config);
      
      context = config.getServletContext();
   }
   
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
   {     
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      
      try
      {
         Lifecycle.beginRequest(context, request.getSession(), request);
         
         // TODO - The captchaId should come from conversation scope
         String captchaId = request.getSession().getId();
         
         BufferedImage challenge = CaptchaService.instance().getImageChallengeForID(
                  captchaId, request.getLocale());
         
         ImageIO.write(challenge, "jpeg", out);
      }
      catch (IllegalArgumentException e)
      {
         response.sendError(HttpServletResponse.SC_NOT_FOUND);
         return;
      }
      catch (CaptchaServiceException e)
      {
         response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         return;
      }
      finally
      {
         Lifecycle.endRequest();  
      }
      
      response.setHeader("Cache-Control", "no-store");
      response.setHeader("Pragma", "no-cache");
      response.setDateHeader("Expires", 0);
      response.setContentType("image/jpeg");
      response.getOutputStream().write(out.toByteArray());
      response.getOutputStream().flush();
      response.getOutputStream().close();
   }
}
