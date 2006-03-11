package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.Locale;

import javax.faces.context.FacesContext;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

/**
 * Selects the current user's locale
 * 
 * @author Gavin King
 */
@Scope(ScopeType.SESSION)
@Name("localeSelector")
@Intercept(NEVER)
public class LocaleSelector {
   private static final Logger log = Logger.getLogger(LocaleSelector.class);

   private String language;
   private String country;
   private String variant;
   
   private Locale locale;
   
   @Create
   public void init()
   {
      setLocale();
      log.debug( "initial locale: " + locale );
   }
   
   public void select()
   {
      setLocale();
      log.debug( "selected locale: " + locale );
      //force the resource bundle to reload
      Contexts.removeFromAllContexts( Seam.getComponentName(ResourceBundle.class) );
      Contexts.removeFromAllContexts( Seam.getComponentName(Messages.class) );
   }

   protected void setLocale() {
      if (variant!=null)
      {
         locale = new java.util.Locale(language, country, variant);
      }
      else if (country!=null)
      {
         locale = new java.util.Locale(language, country);
      }
      else if (language!=null)
      {
         locale = new java.util.Locale(language);
      }
      else
      {
         locale = java.util.Locale.getDefault();
         FacesContext facesContext = FacesContext.getCurrentInstance();
         if (facesContext!=null)
         {
            java.util.Locale defaultLocale = facesContext.getApplication().getDefaultLocale();
            if (defaultLocale!=null) locale = defaultLocale;
            java.util.Locale requestLocale = facesContext.getExternalContext().getRequestLocale();
            if (requestLocale!=null) locale = requestLocale;
         }
      }
      language = locale.getLanguage();
      country = locale.getCountry();
      variant = locale.getVariant();
   }
   
   public static LocaleSelector instance()
   {
      if ( !Contexts.isSessionContextActive() )
      {
         throw new IllegalStateException("No active session context");
      }
      return (LocaleSelector) Component.getInstance( Seam.getComponentName(LocaleSelector.class), ScopeType.SESSION, true );
   }

   public String getCountry() {
      return country;
   }

   public void setCountry(String country) {
      this.country = country;
   }

   public String getLanguage() {
      return language;
   }

   public void setLanguage(String language) {
      this.language = language;
   }

   public String getVariant() {
      return variant;
   }

   public void setVariant(String variant) {
      this.variant = variant;
   }

   public Locale getLocale() {
      return locale;
   }
}
