package org.jboss.seam.captcha;

import java.io.Serializable;
import java.util.Random;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

/**
 * Default CAPTCHA algorithm, a simple addition problem. May be
 * extended and customized.
 * 
 * @author Gavin King
 *
 */
@Name("org.jboss.seam.captcha.captcha")
@Scope(ScopeType.SESSION)
@Install(dependencies="org.jboss.seam.captcha.captchaImage", precedence=Install.BUILT_IN)
@BypassInterceptors
public class Captcha implements Serializable
{
   private static Random random = new Random( System.currentTimeMillis() );
   
   private String correctResponse;
   private String challenge;
   private transient String response;
   
   /**
    * Initialize the challenge and correct response.
    * May be overridden and customized by a subclass.
    */
   @Create
   public void init()
   {
       int x = random.nextInt(50);
       int y = random.nextInt(50);
       setCorrectResponse( Integer.toString( x + y ) );
       setChallenge( Integer.toString(x) + " + " + Integer.toString(y) + " =" );
   }
   
   /**
    * Set the challenge question
    */
   protected void setChallenge(String challenge) 
   {
       this.challenge = challenge;
   }
   
   /**
    * Get the challenge question
    */
   protected String getChallenge()
   {
       return challenge;
   }
   
   /**
    * Set the correct response
    */
   protected void setCorrectResponse(String correctResponse) 
   {
       this.correctResponse = correctResponse;
   }
   
   /**
    * Validate that the entered response is the correct
    * response
    */
   protected boolean validateResponse(String response)
   {
      boolean valid = response!=null && 
                      correctResponse!=null && 
                      response.trim().equals(correctResponse);
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
      if ( !Contexts.isSessionContextActive() )
      {
         throw new IllegalStateException("No page context active");
      }
      return (Captcha) Component.getInstance(Captcha.class, ScopeType.SESSION);
   }

}
