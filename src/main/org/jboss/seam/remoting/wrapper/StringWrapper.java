package org.jboss.seam.remoting.wrapper;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * String wrapper class.
 *
 * @author Shane Bryzak
 */
public class StringWrapper extends BaseWrapper implements Wrapper
{
  /**
   *
   * @param targetClass Class
   * @return Object
   */
  public Object convert(Type type)
      throws ConversionException
  {
    if (type.equals(String.class))
      value = element.getStringValue();
    else if (type.equals(StringBuilder.class))
      value = new StringBuilder(element.getStringValue());
    else if (type.equals(StringBuffer.class))
      value = new StringBuffer(element.getStringValue());
    else if (type.equals(Integer.class))
      value = Integer.valueOf(element.getStringValue());
    else if (type.equals(Integer.TYPE))
      value = Integer.parseInt(element.getStringValue());
    else if (type.equals(Long.class))
      value =  Long.valueOf(element.getStringValue());
    else if (type.equals(Long.TYPE))
      value =  Long.parseLong(element.getStringValue());
    else if (type.equals(Short.class))
      value =  Short.valueOf(element.getStringValue());
    else if (type.equals(Short.TYPE))
      value =  Short.parseShort(element.getStringValue());
    else if (type.equals(Boolean.class))
      value =  Boolean.valueOf(element.getStringValue());
    else if (type.equals(Boolean.TYPE))
      value =  Boolean.parseBoolean(element.getStringValue());
    else if (type.equals(Double.class))
      value =  Double.valueOf(element.getStringValue());
    else if (type.equals(Double.TYPE))
      value =  Double.parseDouble(element.getStringValue());
    else if (type.equals(Float.class))
      value =  Float.valueOf(element.getStringValue());
    else if (type.equals(Float.TYPE))
      value =  Float.parseFloat(element.getStringValue());
    else if (type.equals(Character.class))
      value =  Character.valueOf(element.getStringValue().charAt(0));
    else if (type.equals(Character.TYPE))
      value =  element.getStringValue().charAt(0);
    else if (type.equals(Byte.class))
      value =  Byte.valueOf(element.getStringValue());
    else if (type.equals(Byte.TYPE))
      value =  Byte.parseByte(element.getStringValue());
    else if (type instanceof Class && ((Class) type).isEnum())
      value =  Enum.valueOf((Class) type, element.getStringValue());
    else
      // Should never reach this line - calcConverstionScore should guarantee this.
      throw new ConversionException(String.format(
          "Value [%s] cannot be converted to type [%s].",
          element.getStringValue(), type));

    return value;
  }

  /**
   *
   * @param target Class
   * @return int
   */
  public ConversionScore conversionScore(Class cls)
  {
    if (cls.equals(String.class))
      return ConversionScore.exact;

    String value = (String) this.value;

    if (cls.equals(Integer.class) || cls.equals(Integer.TYPE) ||
        cls.equals(Long.class) || cls.equals(Long.TYPE) ||
        cls.equals(Short.class) || cls.equals(Short.TYPE) ||
        cls.equals(Boolean.class) || cls.equals(Boolean.TYPE) ||
        cls.equals(Double.class) || cls.equals(Double.TYPE) ||
        cls.equals(Float.class) || cls.equals(Float.TYPE) ||
        cls.equals(Character.class) || cls.equals(Character.TYPE) ||
        cls.equals(Byte.class) || cls.equals(Byte.TYPE))
      return ConversionScore.compatible;

    if (cls.isEnum()) {
      Enum.valueOf(cls, value);
      return ConversionScore.compatible;
    }

    return ConversionScore.nomatch;
  }

  /**
   *
   * @return String
   */
  public void marshal(OutputStream out)
    throws IOException
  {
    out.write("<str>".getBytes());

    if (value.getClass().isEnum())
      out.write(((Enum) value).toString().getBytes());
    else
      out.write(((String) value).getBytes());

    out.write("</str>".getBytes());
  }
}
