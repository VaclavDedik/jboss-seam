/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

import org.jboss.seam.annotations.ScopeType;
import org.jboss.seam.deployment.SeamModule;

/**
 * A Seam component is any POJO managed by Seam.
 * A POJO is recognized as a Seam component if it is using the org.jboss.seam.annotations.Name annotation
 * 
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class SeamComponent
{
//   private boolean managedBean = false;
   
   private boolean stateless = false;

   private boolean stateful = false;

   private boolean entity = false;

   private String name;
   
   private ScopeType scope;
   
   private Class bean;

   private SeamModule seamModule;

   public SeamComponent(SeamModule seamModule, Class clazz)
   {
      this.seamModule = seamModule;  
      this.bean = clazz;
   }

   public Class getBean()
   {
      return bean;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public boolean isStateless()
   {
      return stateless;
   }

   public void setStateless(boolean stateless)
   {
      this.stateless = stateless;
   }

   public boolean isEntity()
   {
      return entity;
   }

   public void setEntity(boolean entity)
   {
      this.entity = entity;
   }

   public boolean isStateful()
   {
      return stateful;
   }

   public void setStateful(boolean stateful)
   {
      this.stateful = stateful;
   }

   public SeamModule getSeamModule()
   {
      return seamModule;
   }

   public ScopeType getScope()
   {
      return scope;
   }

   public void setScope(ScopeType scope)
   {
      this.scope = scope;
   }

}
