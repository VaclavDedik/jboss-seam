package org.jboss.seam.security.acl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;

import org.jboss.seam.annotations.Name;

/**
 * Generates ACL identities for JPA entity classes.  The toString() method of
 * the value returned by the identifier method (or field) should return an
 * accurate representation of the identifier's value.  In the case of composite
 * identifiers, a recommended approach is to return the constituent values in
 * delimited form.
 *
 * @author Shane Bryzak
 */
public class JPAIdentityGenerator implements IdentityGenerator
{
  private enum IdentityType {field, method}

  /**
   * Used to cache identity metadata for a Class
   */
  private class IdentityMetadata {
    IdentityType idType;

    String name;

    Field identityField;
    Method identityMethod;

    public Object getIdentityValue(Object obj)
    {
      switch (idType)
      {
        case field:
          try
          {
            return identityField.get(obj);
          }
          catch (IllegalAccessException ex) { // shouldn't occur
            throw new IdentityException(String.format(
                "IllegalAccessException reading identifier field on object [%s]",
                obj), ex);
          }
        case method:
          try
          {
            return identityMethod.invoke(obj);
          }
          catch (IllegalAccessException ex) {
            throw new IdentityException(String.format(
                "IllegalAccessException invoking identifier method on object [%s]",
                obj), ex);
          }
          catch (InvocationTargetException ex) {
            throw new IdentityException(String.format(
                "InvocationTargetException invoking identifier method on object [%s]",
                obj), ex);
          }
        default:
          throw new IllegalStateException("Invalid identifier type");
      }
    }
  }

  /**
   * Cache of identity metadata for Classes
   */
  private Map<Class,IdentityMetadata> identityMeta = new HashMap<Class,IdentityMetadata>();

  /**
   * Returns a String representation of an object's identity.  If the
   * object has no identity, null is returned.
   *
   * @param obj Object The object to return an identity for.
   * @return String The generated identity for the specified object, or null if
   * the object has no identity.
   */
  public String generateIdentity(Object obj)
  {
    IdentityMetadata meta = getIdentityMetadata(obj.getClass());

    Object val = meta.getIdentityValue(obj);

    return val == null ? null : String.format("%s:%s", meta.name, val.toString());
  }

  /**
   * Returns the identity metadata for the specified Class.
   *
   * @param cls Class
   * @return IdentityMetadata
   */
  private IdentityMetadata getIdentityMetadata(Class cls)
  {
    if (!identityMeta.containsKey(cls))
    {
      synchronized(identityMeta)
      {
        if (!identityMeta.containsKey(cls))
        {
          IdentityMetadata meta = new IdentityMetadata();

          // Check for a method annotated with @Id
          for (Method m : cls.getMethods())
          {
            if (m.isAnnotationPresent(Id.class))
            {
              if (m.getParameterTypes().length > 0)
                throw new IllegalArgumentException(String.format(
                    "Specified class [%s] has illegal identifier method - must accept no parameters.",
                    cls.getName()));

              meta.idType = IdentityType.method;
              meta.identityMethod = m;

              m.setAccessible(true);
              break;
            }
          }

          // If there is no @Id method, check the fields
          if (meta.identityMethod == null)
          {
            for (Field f : cls.getDeclaredFields())
            {
              System.out.println("Field: " + f.getName());
              if (f.isAnnotationPresent(Id.class))
              {
                meta.idType = IdentityType.field;
                meta.identityField = f;

                // Have to do this so we can read the field value
                f.setAccessible(true);
                break;
              }
            }
          }

          if (meta.identityField == null && meta.identityMethod == null)
            throw new IllegalArgumentException(String.format(
                "Specified class [%s] has no identifier method or field.", cls.getName()));

          if (cls.isAnnotationPresent(Name.class))
            meta.name = ((Name) cls.getAnnotation(Name.class)).value();
          else
            meta.name = cls.getName();

          identityMeta.put(cls, meta);
        }
      }
    }

    return identityMeta.get(cls);
  }
}
