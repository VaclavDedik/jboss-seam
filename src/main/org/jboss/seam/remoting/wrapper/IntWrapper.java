package org.jboss.seam.remoting.wrapper;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * Int wrapper class.
 *
 * @author Shane Bryzak
 */
public class IntWrapper extends BaseWrapper implements Wrapper
{
  /**
   *
   * @param cls Class
   * @return Object
   */
  public Object convert(Type type)
      throws ConversionException
  {
    String val = element.getStringValue().trim();

    if (type.equals(Integer.class))
      value = !"".equals(val) ? Integer.valueOf(val) : null;
    else if (type.equals(Integer.TYPE))
      value = Integer.parseInt(val);
    else if (type.equals(Long.class))
      value = !"".equals(val) ? Long.valueOf(val) : null;
    else if (type.equals(Long.TYPE))
      value = Long.parseLong(val);
    else if (type.equals(String.class))
      value = val;
    else
      throw new ConversionException(String.format(
        "Value [%s] cannot be converted to type [%s].", element.getStringValue(),
        type));

    return value;
  }

  /**
   *
   * @param out OutputStream
   * @throws IOException
   */
  public void marshal(OutputStream out)
    throws IOException
  {
    out.write("<int>".getBytes());
    out.write(value.toString().getBytes());
    out.write("</int>".getBytes());
  }

  /**
   * Allow conversions to either Integer or String.
   *
   * @param cls Class
   * @return ConversionScore
   */
  public ConversionScore conversionScore(Class cls)
  {
    if (cls.equals(Integer.class) || cls.equals(Integer.TYPE))
      return ConversionScore.exact;

    if (cls.equals(String.class))
      return ConversionScore.compatible;

    return ConversionScore.nomatch;
  }
}
