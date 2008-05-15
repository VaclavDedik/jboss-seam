package org.jboss.seam.ui.facelet;

import static org.jboss.seam.ScopeType.SESSION;

import javax.servlet.http.HttpSession;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.mock.MockHttpSession;

@Name("org.jboss.seam.ui.facelet.mockHttpSession")
@Scope(SESSION)
@BypassInterceptors
@Install(dependencies="org.jboss.seam.faces.renderer")
@AutoCreate
public class MockHttpSessionManager
{
   
   private HttpSession session;
   
   @Create
   public void create()
   {
      this.session = new MockHttpSession(MockServletContextManager.instance());
   }
   
   @Unwrap
   public HttpSession getSession()
   {
      return session;
   }
   
   public static HttpSession instance()
   {
      if (!Contexts.isSessionContextActive())
      {
         throw new IllegalStateException("Session context is not active");
      }
      return (HttpSession) Component.getInstance(MockHttpSessionManager.class, SESSION);
   }

}
