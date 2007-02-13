package org.jboss.seam.captcha;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.ResourceProvider;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.servlet.AbstractResourceProvider;

import com.octo.captcha.service.CaptchaServiceException;

/**
 * Provides Captcha image resources
 * 
 * @author Shane Bryzak
 */
@Startup
@Scope(APPLICATION)
@Name("org.jboss.seam.captcha.captchaResourceProvider")
@Install(precedence = BUILT_IN)
@Intercept(NEVER)
@ResourceProvider("/captcha")
public class CaptchaResourceProvider extends AbstractResourceProvider
{
   @Override
   public void getResource(HttpServletRequest request, HttpServletResponse response)
       throws IOException
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      try
      {
         Lifecycle.beginRequest(getServletContext(), request.getSession(), request);

         String captchaId = request.getQueryString();

         BufferedImage challenge = CaptchaService.instance().getService().getImageChallengeForID(
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
