package org.jboss.seam.international;

import java.io.Serializable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.jboss.seam.core.Interpolator;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.util.Strings;

/**
 * A status message which can be created in the business layer and displayed
 * in the view layer
 *
 * @author Pete Muir
 *
 */
public class StatusMessage implements Serializable
{
   
   /**
    * The severity of the status message
    *
    */
   public enum Severity
   {
      INFO, 
      WARN, 
      ERROR, 
      FATAL;
   }
   
   private String summary;
   private String detail;
   private Severity severity = Severity.INFO;
   
   /**
    * Create a status message, looking up the message in the resource bundle
    * using the provided key. If the message is found, it is used, otherwise, 
    * the defaultMessageTemplate will be used.
    * 
    */
   public StatusMessage(Severity severity, String key, String detailKey, String defaultMessageTemplate, String defaultMessageDetailTemplate, Object... params)
   {
      String messageTemplate = getBundleMessage(key, defaultMessageTemplate);
      String messageDetailTemplate = getBundleMessage(detailKey, defaultMessageDetailTemplate);
      if ( !Strings.isEmpty(messageTemplate) )
      {
         this.severity = severity;
         this.summary = Interpolator.instance().interpolate(messageTemplate, params);
         if (!Strings.isEmpty(messageDetailTemplate))
         {
            this.detail = Interpolator.instance().interpolate(messageDetailTemplate, params);
         }
      }
   }
   
   public StatusMessage(String summary, String detail, Severity severity)
   {
      this.summary = summary;
      this.detail = detail;
      this.severity = severity;
   }

   /**
    * Get the message
    * 
    */
   public String getSummary()
   {
      return summary;
   }
   
   /**
    * Get the message severity
    */
   public Severity getSeverity()
   {
      return severity;
   }
   
   public String getDetail()
   {
      return detail;
   }
   
   public static String getBundleMessage(String key, String defaultMessageTemplate)
   {
      String messageTemplate = defaultMessageTemplate;
      if ( key!=null )
      {
         ResourceBundle resourceBundle = SeamResourceBundle.getBundle();
         if ( resourceBundle!=null ) 
         {
            try
            {
               String bundleMessage = resourceBundle.getString(key);
               if (bundleMessage!=null) 
               {
                  messageTemplate = bundleMessage;
               }
            }
            catch (MissingResourceException mre) {} //swallow
         }
      }
      return messageTemplate;
   }
   
   @Override
   public String toString()
   {
      return "[" + severity + "] " + summary + " (" + detail +")";
   }
   
}
