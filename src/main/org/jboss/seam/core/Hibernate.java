//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;

/**
 * A seam component that boostraps a Hiberate SessionFactory
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup
@Name("org.jboss.seam.core.hibernate")
public class Hibernate
{
   public static final String CLASS_NAMES = Seam.getComponentName(Hibernate.class) + ".classNames";
   
   private String[] classNames;
   private SessionFactory sf;
   
   @Unwrap
   public SessionFactory getSessionFactory()
   {
      return sf;
   }
   
   @Create
   public void startup() throws Exception
   {
      //force JNDI and TM startup
      Component.getInstance( Seam.getComponentName(Jndi.class), true );
      Component.getInstance( Seam.getComponentName(Tm.class), true );
      
      AnnotationConfiguration acfg = new AnnotationConfiguration();
      
      //force datasource startup
      String datasourceName = acfg.getProperty(Environment.DATASOURCE);
      Component.getInstance( datasourceName, true );
      
      for (String clazz: classNames)
      {
         acfg.addAnnotatedClass( Class.forName(clazz) );
      }
      sf = acfg.buildSessionFactory();
      
   }
   
   @Destroy
   public void shutdown()
   {
      sf.close();
   }

   public String[] getClassNames()
   {
      return classNames;
   }

   public void setClassNames(String[] classNames)
   {
      this.classNames = classNames;
   }

}
