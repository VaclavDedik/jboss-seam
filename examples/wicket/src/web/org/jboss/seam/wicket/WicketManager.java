package org.jboss.seam.wicket;


import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Manager;

@Scope(ScopeType.EVENT)
@Name("org.jboss.seam.core.manager")
@Install(precedence=100, classDependencies="org.apache.wicket.Application")
@BypassInterceptors
public class WicketManager extends Manager
{
   
   private boolean controllingRedirect;
   
   public String appendConversationIdFromRedirectFilter(String url, String viewId)
   {
      boolean appendConversationId = !controllingRedirect;
      if (appendConversationId)
      {
         beforeRedirect(viewId);         
         url = encodeConversationId(url, viewId);
      }
      return url;
   }
   
   /**
    * Temporarily promote a temporary conversation to
    * a long running conversation for the duration of
    * a browser redirect. After the redirect, the 
    * conversation will be demoted back to a temporary
    * conversation. Handle any changes to the conversation
    * id, due to propagation via natural id.
    */
   public void beforeRedirect(String viewId)
   {
      // TODO - do something here!
   }
   
   public static WicketManager instance()
   {
      return (WicketManager) Manager.instance();
   }
}
