package org.jboss.seam.security.permission;

/**
 * An Identifier strategy for class-based permission checks
 * 
 * @author Shane Bryzak
 */
public class ClassIdentifierStrategy implements IdentifierStrategy
{
   public boolean canIdentify(Class targetClass)
   {
      return Class.class.equals(targetClass);
   }

   public String getIdentifier(Object target)
   {
      // TODO Auto-generated method stub
      return null;
   }

}
