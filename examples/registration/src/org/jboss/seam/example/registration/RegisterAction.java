//$Id$
package org.jboss.seam.example.registration;

import static org.jboss.seam.ScopeType.EVENT;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateless
@Scope(EVENT)
@Name("register")
@Interceptor(SeamInterceptor.class)
public class RegisterAction implements Register
{

   @In
   private User user;
   
   @PersistenceContext
   private EntityManager em;
   
   public String register()
   {
      em.persist(user);
      return "login";
   }

}
