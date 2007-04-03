package org.jboss.seam.trinidad;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.STATELESS;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import javax.faces.model.DataModel;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.Query;
import org.jboss.seam.jsf.JsfProvider;

@Name("org.jboss.seam.jsf.jsfProvider")
@Install(precedence=FRAMEWORK, classDependencies="org.apache.myfaces.trinidad.model.CollectionModel")
@Scope(STATELESS)
@Intercept(NEVER)
public class TrinidadJsfProvider extends JsfProvider
{
   
   @Override
   public DataModel getDataModel(Query query)
   {
      return new TrinidadCollectionModel(query);
   }
   
   
}
