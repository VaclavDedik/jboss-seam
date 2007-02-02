package org.jboss.seam.security;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.contexts.Contexts;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;

@Name("org.jboss.seam.security.captcha")
@Intercept(InterceptionType.NEVER)
@Scope(ScopeType.APPLICATION)
public class CaptchaService
{
   private ImageCaptchaService service;
   
   @Create
   public void create()
   {
      service = new DefaultManageableImageCaptchaService();
   }
   
   @Unwrap
   public ImageCaptchaService getService()
   {
      return service;
   }
   
   public static ImageCaptchaService instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application scope");
      }
      return (ImageCaptchaService) Component.getInstance(CaptchaService.class);
   }   
}
