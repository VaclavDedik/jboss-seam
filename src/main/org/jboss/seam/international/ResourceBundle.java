package org.jboss.seam.international;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Support for an application-global resource bundle
 * 
 * @author Gavin King
 */
@Scope(ScopeType.SESSION)
@Intercept(NEVER)
@Name("org.jboss.seam.core.resourceBundle")
@Install(precedence=FRAMEWORK, dependencies="org.jboss.seam.international.locale")
public class ResourceBundle extends org.jboss.seam.core.ResourceBundle
{
   @Override
   protected java.util.Locale getCurrentLocale()
   {
      return Locale.instance();
   }
}
