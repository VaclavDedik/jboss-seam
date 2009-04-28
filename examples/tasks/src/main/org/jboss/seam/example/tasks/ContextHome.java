package org.jboss.seam.example.tasks;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.tasks.entity.Context;
import org.jboss.seam.framework.EntityHome;

@Name("contextHome")
@AutoCreate
public class ContextHome extends EntityHome<Context>
{

   public Context findByUsernameAndContext(String username, String context)
   {
      setInstance((Context) getEntityManager().createNamedQuery("contextByNameAndUser").setParameter("username", username).setParameter("context", context).getSingleResult());
      return getInstance();
   }

}
