package org.jboss.seam.ui.converter.entityConverter;

import static org.jboss.seam.ScopeType.STATELESS;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityIdentifier;
import org.jboss.seam.framework.Identifier;

/**
 * Stores entity identifiers under a key, which can be used on a page
 *
 * @author Pete Muir
 */

@Name("org.jboss.seam.ui.entityLoader")
@Install(precedence=BUILT_IN)
@Scope(STATELESS)
public class EntityLoader extends AbstractEntityLoader<EntityManager>
{
     
   @Override
   public EntityManager getPersistenceContext()
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
      return new EntityIdentifier(entity, getPersistenceContext());
   }

   @Override
   protected String getPersistenceContextName()
   {
      return "entityManager";
   }
   
   public static EntityLoader instance()
   {
      return (EntityLoader) Component.getInstance(EntityLoader.class, STATELESS);
   }

}
