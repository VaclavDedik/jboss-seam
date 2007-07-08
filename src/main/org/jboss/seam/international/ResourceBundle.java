package org.jboss.seam.international;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * A resource bundle aware of the selected locale
 * 
 * @author Gavin King
 */
@Scope(ScopeType.SESSION)
@BypassInterceptors
@Name("org.jboss.seam.core.resourceBundleFactory")
@Install(precedence=FRAMEWORK, dependencies="org.jboss.seam.international.locale")
public class ResourceBundle extends org.jboss.seam.core.ResourceBundle
{
   @Override
   protected java.util.Locale getCurrentLocale()
   {
      return Locale.instance();
   }
}
