package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.Locale;

import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Strings;

/**
 * Selects the current user's locale
 * 
 * @author Gavin King
 */
@Scope(ScopeType.SESSION)
@Name("localeSelector")
@Intercept(NEVER)
public class LocaleSelector {

   private String language;
   private String country;
   private String variant;
   
   /**
    * Force the resource bundle to reload, using the current locale
    */
   public void select()
   {
      FacesContext.getCurrentInstance().getViewRoot().setLocale( getLocale() );
      Contexts.removeFromAllContexts( Seam.getComponentName(ResourceBundle.class) );
      Contexts.removeFromAllContexts( Seam.getComponentName(Messages.class) );
   }
   
   public Locale calculateLocale(Locale jsfLocale)
   {
      if ( !Strings.isEmpty(variant) )
      {
         return new java.util.Locale(language, country, variant );
      }
      else if ( !Strings.isEmpty(country) )
      {
         return new java.util.Locale(language, country);
      }
      else if ( !Strings.isEmpty(language) )
      {
         return new java.util.Locale(language);
      }
      else
      {
         return jsfLocale;
      }
   }

   public Locale getLocale() 
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if (facesContext==null)
      {
         return calculateLocale( Locale.getDefault() );
      }
      else
      {
         return facesContext.getApplication().getViewHandler().calculateLocale( facesContext );
      }
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
      if (country==null) return getLocale().getCountry();
      return country;
   }

   public void setCountry(String country) {
      this.country = country;
   }

   public String getLanguage() {
      if (language==null) return getLocale().getLanguage();
      return language;
   }

   public void setLanguage(String language) {
      this.language = language;
   }

   public String getVariant() {
      if (variant==null) return getLocale().getVariant();
      return variant;
   }

   public void setVariant(String variant) {
      this.variant = variant;
   }
   
}
