package org.jboss.seam.ui.converter.entityConverter;

import static org.jboss.seam.ScopeType.STATELESS;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import org.hibernate.Session;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.HibernateEntityIdentifier;
import org.jboss.seam.framework.Identifier;

/**
 * Stores entity identifiers under a key, which can be used on a page
 *
 * @author Pete Muir
 */

@Name("org.jboss.seam.ui.hibernateEntityLoader")
@Install(precedence=BUILT_IN, classDependencies="org.hibernate.Session")
@Scope(STATELESS)
public class HibernateEntityLoader extends AbstractEntityLoader<Session>
{
     
   @Override
   public Session getPersistenceContext()
   {
      if (!super.getPersistenceContext().isOpen())
      {
         super.setPersistenceContext(null);
      }
      return super.getPersistenceContext();
   }

   @Override
   protected Identifier createIdentifier(Object entity)
   {
      return new HibernateEntityIdentifier(entity, getPersistenceContext());
   }

   @Override
   protected String getPersistenceContextName()
   {
      return "session";
   }
   
   public static HibernateEntityLoader instance()
   {
      return (HibernateEntityLoader) Component.getInstance(HibernateEntityLoader.class, STATELESS);
   }
}
