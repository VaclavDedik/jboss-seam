//$Id$
package org.jboss.seam.core;


import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

/**
 * A Seam component that holds Seam configuration settings
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Name("org.jboss.seam.core.init")
public class Init
{
   private static final String NAME = Seam.getComponentName(Init.class);
   public static final String COMPONENT_CLASS_NAMES = NAME + ".componentClassNames";
   public static final String PERSISTENCE_UNIT_NAMES = NAME + ".persistenceUnitNames";
   public static final String SESSION_FACTORY_NAMES = NAME + ".sessionFactoryNames";
   public static final String JBPM_SESSION_FACTORY_NAME = NAME + ".jbpmSessionFactoryName";
   
   private String[] persistenceUnitNames = {};
   private String[] sessionFactoryNames = {};
   private String jbpmSessionFactoryName;
   private String[] componentClassNames = {};

   public String[] getPersistenceUnitNames()
   {
      return persistenceUnitNames;
   }
   public void setPersistenceUnitNames(String[] persistenceUnitNames)
   {
      this.persistenceUnitNames = persistenceUnitNames;
   }

   public String[] getSessionFactoryNames()
   {
      return sessionFactoryNames;
   }

   public void setSessionFactoryNames(String[] sessionFactoryNames)
   {
      this.sessionFactoryNames = sessionFactoryNames;
   }

   public String getJbpmSessionFactoryName()
   {
      return jbpmSessionFactoryName;
   }

   public void setJbpmSessionFactoryName(String jbpmSessionFactoryName)
   {
      this.jbpmSessionFactoryName = jbpmSessionFactoryName;
   }

   public String[] getComponentClassNames()
   {
      return componentClassNames;
   }

   public void setComponentClassNames(String[] componentClassNames)
   {
      this.componentClassNames = componentClassNames;
   }
   
   public static Init instance()
   {
      return (Init) Contexts.getApplicationContext().get(Init.class);
   }
   
}
