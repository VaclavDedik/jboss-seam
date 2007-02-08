package org.jboss.seam.captcha;

import java.io.Serializable;
import java.rmi.server.UID;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

@Name("org.jboss.seam.captcha.captcha")
@Scope(ScopeType.PAGE)
@Install(dependencies="org.jboss.seam.captcha.captchaService")
public class Captcha implements Serializable
{
   private String id;
   private transient String response;
   
   @Create
   public void init()
   {
      id =  new UID().toString().replace(":", "-");
   }
   
   boolean validateResponse(String response)
   {
      boolean valid = CaptchaService.instance().getService().validateResponseForID(id, response);
      if (!valid) 
      {
         init();
      }
      return valid;
   }

   @CaptchaResponse
   public String getResponse()
   {
      return response;
   }

   public void setResponse(String input)
   {
      this.response = input;
   }
   
   public static Captcha instance()
   {
      if ( !Contexts.isPageContextActive() )
      {
         throw new IllegalStateException("No page context active");
      }
      return (Captcha) Component.getInstance(Captcha.class, ScopeType.PAGE);
   }

   public String getId()
   {
      return id;
   }

}
