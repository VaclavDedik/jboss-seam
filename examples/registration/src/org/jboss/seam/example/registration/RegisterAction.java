//$Id$
package org.jboss.seam.example.registration;

import static org.jboss.seam.ScopeType.EVENT;

import javax.ejb.Interceptor;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Scope(EVENT)
@Name("register")
@LocalBinding(jndiBinding="register")
@Interceptor(SeamInterceptor.class)
public class RegisterAction implements Register
{
   
   private static final Logger log = Logger.getLogger(Register.class);

   @In
   private User user;
   
   @PersistenceContext
   private EntityManager em;
   
   public String register()
   {
      em.persist(user);
      return "login";
   }

   @Destroy @Remove
   public void destroy()
   {
      log.info("destroyed");
   }
}
