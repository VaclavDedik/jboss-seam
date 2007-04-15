package org.jboss.seam.trinidad;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.STATELESS;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import javax.faces.model.DataModel;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.framework.HibernateEntityQuery;
import org.jboss.seam.framework.Query;
import org.jboss.seam.jsf.JsfProvider;

/**
 * Provide enhanced features when Trinidad is used as a JSF component set
 * @author pmuir
 *
 */

@Name("org.jboss.seam.jsf.jsfProvider")
@Install(precedence=FRAMEWORK, classDependencies="org.apache.myfaces.trinidad.component.UIXComponent")
@Scope(STATELESS)
@Intercept(NEVER)
public class TrinidadJsfProvider extends JsfProvider
{
   
   @Override
   public DataModel getDataModel(Query query)
   {
      // If an EntityQuery is in use we can return a CollectionModel
      // backed by the database
      if (query instanceof EntityQuery)
      {
         return new EntityCollectionModel((EntityQuery) query);
      }
      else if (query instanceof HibernateEntityQuery)
      {
         return new HibernateEntityCollectionModel((HibernateEntityQuery) query);
      }
      else
      {
         return super.getDataModel(query);
      }
   }
   
   
}
