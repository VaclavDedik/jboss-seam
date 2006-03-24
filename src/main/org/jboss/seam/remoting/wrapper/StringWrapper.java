package org.jboss.seam.remoting.wrapper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * String wrapper class.
 *
 * @author Shane Bryzak
 */
public class StringWrapper extends BaseWrapper implements Wrapper
{
  private static final String DEFAULT_ENCODING = "ISO-8859-1";

  /**
   *
   * @param targetClass Class
   * @return Object
   */
  public Object convert(Type type)
      throws ConversionException
  {
    String elementValue = null;
    try {
      elementValue = URLDecoder.decode(element.getStringValue(),
                                              DEFAULT_ENCODING);
    }
    catch (UnsupportedEncodingException ex) {
      throw new ConversionException("Error converting value - encoding not supported.");
    }

    if (type.equals(String.class))
      value = elementValue;
    else if (type.equals(StringBuilder.class))
      value = new StringBuilder(elementValue);
    else if (type.equals(StringBuffer.class))
      value = new StringBuffer(elementValue);
    else if (type.equals(Integer.class))
      value = Integer.valueOf(elementValue);
    else if (type.equals(Integer.TYPE))
      value = Integer.parseInt(elementValue);
    else if (type.equals(Long.class))
      value =  Long.valueOf(elementValue);
    else if (type.equals(Long.TYPE))
      value =  Long.parseLong(elementValue);
    else if (type.equals(Short.class))
      value =  Short.valueOf(elementValue);
    else if (type.equals(Short.TYPE))
      value =  Short.parseShort(elementValue);
    else if (type.equals(Boolean.class))
      value =  Boolean.valueOf(elementValue);
    else if (type.equals(Boolean.TYPE))
      value =  Boolean.parseBoolean(elementValue);
    else if (type.equals(Double.class))
      value =  Double.valueOf(elementValue);
    else if (type.equals(Double.TYPE))
      value =  Double.parseDouble(elementValue);
    else if (type.equals(Float.class))
      value =  Float.valueOf(elementValue);
    else if (type.equals(Float.TYPE))
      value =  Float.parseFloat(elementValue);
    else if (type.equals(Character.class))
      value =  Character.valueOf(elementValue.charAt(0));
    else if (type.equals(Character.TYPE))
      value =  elementValue.charAt(0);
    else if (type.equals(Byte.class))
      value =  Byte.valueOf(elementValue);
    else if (type.equals(Byte.TYPE))
      value =  Byte.parseByte(elementValue);
    else if (type instanceof Class && ((Class) type).isEnum())
      value =  Enum.valueOf((Class) type, elementValue);
    else
      // Should never reach this line - calcConverstionScore should guarantee this.
      throw new ConversionException(String.format(
          "Value [%s] cannot be converted to type [%s].", elementValue, type));

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
    out.write(URLEncoder.encode(value.toString(), DEFAULT_ENCODING).getBytes());
    out.write("</str>".getBytes());
  }
}
