package org.jboss.seam.remoting.wrapper;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

/**
 * @author Shane Bryzak
 */
public class MapWrapper extends BaseWrapper implements Wrapper
{
  private static final byte[] MAP_TAG_OPEN = "<map>".getBytes();
  private static final byte[] MAP_TAG_CLOSE = "</map>".getBytes();

  private static final byte[] ELEMENT_TAG_OPEN = "<element>".getBytes();
  private static final byte[] ELEMENT_TAG_CLOSE = "</element>".getBytes();

  private static final byte[] KEY_TAG_OPEN = "<k>".getBytes();
  private static final byte[] KEY_TAG_CLOSE = "</k>".getBytes();

  private static final byte[] VALUE_TAG_OPEN = "<v>".getBytes();
  private static final byte[] VALUE_TAG_CLOSE = "</v>".getBytes();

  public void marshal(OutputStream out) throws IOException
  {
    out.write(MAP_TAG_OPEN);

    Map m = (Map) this.value;

    for (Object key : m.keySet())
    {
      out.write(ELEMENT_TAG_OPEN);

      out.write(KEY_TAG_OPEN);
      context.createWrapperFromObject(key).marshal(out);
      out.write(KEY_TAG_CLOSE);

      out.write(VALUE_TAG_OPEN);
      context.createWrapperFromObject(m.get(key)).marshal(out);
      out.write(VALUE_TAG_CLOSE);

      out.write(ELEMENT_TAG_CLOSE);
    }

    out.write(MAP_TAG_CLOSE);
  }

  public Object convert(Type type)
      throws ConversionException
  {
    Class typeClass = null;
    Type keyType = null;
    Type valueType = null;

    // Either the type should be a generified Map
    if (type instanceof ParameterizedType &&
        Map.class.isAssignableFrom((Class) ((ParameterizedType) type).getRawType()))
    {
      typeClass = (Class) ((ParameterizedType) type).getRawType();

      for (Type t : ((ParameterizedType) type).getActualTypeArguments())
      {
        if (keyType == null)
          keyType = t;
        else
        {
          valueType = t;
          break;
        }
      }
    }
    // Or a non-generified Map
    else if (type instanceof Class && Map.class.isAssignableFrom((Class) type))
    {
      if (!((Class) type).isInterface())
        typeClass = (Class) type;
    }
    // If it's neither, throw an exception
    else
      throw new ConversionException(String.format(
        "Cannot convert value to type [%s]", type));

    // If we don't have a concrete type, default to creating a HashMap
    if (typeClass == null || typeClass.isInterface())
      value = new HashMap();
    else
    {
      try {
        // Otherwise create an instance of the concrete type
        value = ( (Class) type).newInstance();
      }
      catch (Exception ex) {
        throw new ConversionException(String.format(
            "Could not create value of type [%s]", type));
      }
    }

    for (Element e : (List<Element>) element.elements("element"))
    {
      ((Map) value).put(
        context.createWrapperFromElement((Element) e.element("k").elements().get(0)).convert(keyType),
        context.createWrapperFromElement((Element) e.element("v").elements().get(0)).convert(valueType));
    }

    return value;
  }

  public ConversionScore conversionScore(Class cls)
  {
    if (cls.equals(Map.class))
      return ConversionScore.exact;

    for (Class c : cls.getInterfaces())
    {
      if (c.equals(Map.class))
        return ConversionScore.compatible;
    }

    return ConversionScore.nomatch;
  }
}
