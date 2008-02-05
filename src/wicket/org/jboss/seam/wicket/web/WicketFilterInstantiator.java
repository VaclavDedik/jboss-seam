/**
 * 
 */
package org.jboss.seam.wicket.web;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import javax.servlet.Filter;

import org.apache.wicket.protocol.http.WicketFilter;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Name("org.jboss.seam.wicket.web.wicketFilterInstantiator")
@Install(precedence = BUILT_IN, classDependencies={"org.apache.wicket.Application"})
@BypassInterceptors
@Scope(ScopeType.STATELESS)
public class WicketFilterInstantiator
{

   @Unwrap
   public Filter unrwap()
   {
      return new WicketFilter();
   }

}
