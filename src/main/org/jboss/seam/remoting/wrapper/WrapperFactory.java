package org.jboss.seam.remoting.wrapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Shane Bryzak
 */
public class WrapperFactory
{
  /**
   * Singleton instance.
   */
  private static final WrapperFactory factory = new WrapperFactory();

  /**
   * A registry of wrapper types
   */
  private Map<String,Class> wrapperRegistry = new HashMap<String,Class>();

  /**
   *
   */
  private Map<Class,Class> classRegistry = new HashMap<Class,Class>();

  /**
   * Private constructor
   */
  private WrapperFactory()
  {
    // Register the defaults
    registerWrapper("str", StringWrapper.class);
    registerWrapper("bool", BooleanWrapper.class);
    registerWrapper("bean", BeanWrapper.class);
    registerWrapper("int", IntWrapper.class);
    registerWrapper("null", NullWrapper.class);
    registerWrapper("bag", BagWrapper.class);
    registerWrapper("map", MapWrapper.class);

    registerWrapperClass(String.class, StringWrapper.class);
    registerWrapperClass(StringBuilder.class, StringWrapper.class);
    registerWrapperClass(StringBuffer.class, StringWrapper.class);
    registerWrapperClass(Integer.class, IntWrapper.class);
    registerWrapperClass(Long.class, IntWrapper.class);
  }

  /**
   *
   * @param type String
   * @param wrapperClass Class
   */
  public void registerWrapper(String type, Class wrapperClass)
  {
    wrapperRegistry.put(type, wrapperClass);
  }

  /**
   *
   * @param cls Class
   * @param wrapperClass Class
   */
  public void registerWrapperClass(Class cls, Class wrapperClass)
  {
    classRegistry.put(cls, wrapperClass);
  }

  /**
   *
   * @return WrapperFactory
   */
  public static WrapperFactory getInstance()
  {
    return factory;
  }

  /**
   *
   * @param element Element
   * @return BaseWrapper
   */
  public Wrapper createWrapper(String type)
  {
    Class wrapperClass = wrapperRegistry.get(type);

    if (wrapperClass != null)
    {
      try {
        Wrapper wrapper = (Wrapper) wrapperClass.newInstance();
        return wrapper;
      }
      catch (IllegalAccessException ex) { }
      catch (InstantiationException ex) { }
    }

    throw new RuntimeException(String.format("Failed to create wrapper for type: %s",
                               type));
  }

  /**
   *
   * @param obj Object
   * @return Wrapper
   */
  public Wrapper getWrapperForObject(Object obj)
  {
    if (obj == null)
      return new NullWrapper();

    Wrapper w = null;

    if (Map.class.isAssignableFrom(obj.getClass()))
      w = new MapWrapper();
    else if (obj.getClass().isArray() || Collection.class.isAssignableFrom(obj.getClass()))
      w = new BagWrapper();
    else if (obj.getClass().equals(Boolean.class) || obj.getClass().equals(Boolean.TYPE))
      w = new BooleanWrapper();
    else if (obj.getClass().isEnum())
      w = new StringWrapper();
    else if (classRegistry.containsKey(obj.getClass()))
    {
      try
      {
        w = (Wrapper) classRegistry.get(obj.getClass()).newInstance();
      }
      catch (Exception ex)
      {
        throw new RuntimeException("Failed to create wrapper instance.");
      }
    }
    else
      w = new BeanWrapper();

    w.setValue(obj);
    return w;
  }
}
