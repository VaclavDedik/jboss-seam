package org.jboss.seam.captcha;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.web.AbstractResource;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;

/**
 * Provides Captcha image resources
 * 
 * @author Shane Bryzak
 */
@Startup
@Scope(APPLICATION)
@Name("org.jboss.seam.captcha.captchaImage")
@BypassInterceptors
@Install(precedence = BUILT_IN,  
         classDependencies="com.octo.captcha.service.image.ImageCaptchaService")
public class CaptchaImage extends AbstractResource
{   
   private ImageCaptchaService service;
   
   public static CaptchaImage instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No application context active");
      }
      return (CaptchaImage) Contexts.getApplicationContext().get(CaptchaImage.class);
   }
   
   public boolean validateResponse(String id, String response)
   {
      try
      {
         return service.validateResponseForID(id, response);
      }
      catch (CaptchaServiceException cse)
      {
         return false;
      }
   }
   
   @Create
   public void create()
   {
      if (service == null)
      {
         service = new DefaultManageableImageCaptchaService();
      }
   }
   
   @Override
   public String getResourcePath()
   {
      return "/captcha";
   }
   
   @Override
   public void getResource(HttpServletRequest request, HttpServletResponse response)
       throws IOException
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      ServletLifecycle.beginRequest(request);         
      try
      {
         String captchaId = request.getQueryString();
         BufferedImage challenge = service.getImageChallengeForID( captchaId, request.getLocale() );
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
         ServletLifecycle.endRequest(request);         
      }

      response.setHeader("Cache-Control", "no-store");
      response.setHeader("Pragma", "no-cache");
      response.setDateHeader("Expires", 0);
      response.setContentType("image/jpeg");
      response.getOutputStream().write( out.toByteArray() );
      response.getOutputStream().flush();
      response.getOutputStream().close();
   }
   
   public ImageCaptchaService getService()
   {
      return service;
   }
   
   public void setService(ImageCaptchaService service)
   {
      this.service = service;
   }
}
