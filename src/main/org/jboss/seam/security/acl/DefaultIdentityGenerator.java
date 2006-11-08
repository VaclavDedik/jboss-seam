package org.jboss.seam.security.acl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author Shane Bryzak
 */
public class DefaultIdentityGenerator implements IdentityGenerator
{
  private enum IdentityMethod {field, method};

  private class IdentityMetadata {
    IdentityMethod idMethod;

    String name;

    Field identityField;
    Method identityMethod;

    public Serializable getIdentityValue(Object obj)
    {
      switch (idMethod)
      {
        case field:
          try
          {
            return (Serializable) identityField.get(obj);
          }
          catch (IllegalAccessException ex) { // shouldn't occur
          }
        case method:
        default: throw new IllegalStateException("Invalid identifier type");
      }
    }
  }

  private Map<Class,IdentityMetadata> identityMeta = new HashMap<Class,IdentityMetadata>();

  public String generateIdentity(Object obj)
  {
    IdentityMetadata meta = getIdentityMetadata(obj.getClass());

    meta.identityField.setAccessible(true);
    Object fieldVal = meta.getIdentityValue(obj);
    return String.format("%s:%s", meta.name, fieldVal.toString());
  }

  private IdentityMetadata getIdentityMetadata(Class cls)
  {
    if (!identityMeta.containsKey(cls))
    {
      synchronized(identityMeta)
      {
        if (!identityMeta.containsKey(cls))
        {

        }
      }
    }

    return identityMeta.get(cls);
  }
}
