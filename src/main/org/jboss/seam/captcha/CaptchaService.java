package org.jboss.seam.captcha;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;

@Name("org.jboss.seam.captcha.captchaService")
@Intercept(InterceptionType.NEVER)
@Scope(ScopeType.APPLICATION)
@Install(classDependencies="com.octo.captcha.service.image.ImageCaptchaService")
public class CaptchaService
{
   private ImageCaptchaService service;
   
   @Create
   public void create()
   {
      service = new DefaultManageableImageCaptchaService();
   }
   
   public ImageCaptchaService getService()
   {
      return service;
   }
   
   public static CaptchaService instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application scope");
      }
      return (CaptchaService) Component.getInstance(CaptchaService.class);
   }
}
