package org.jboss.seam.remoting.wrapper;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * @author Shane Bryzak
 */
public class BooleanWrapper extends BaseWrapper implements Wrapper
{
  public void marshal(OutputStream out) throws IOException
  {
    out.write("<bool>".getBytes());
    out.write(((Boolean) value).toString().getBytes());
    out.write("</bool>".getBytes());
  }

  public Object convert(Type type)
    throws ConversionException
  {
    if (type.equals(Boolean.class))
      return Boolean.valueOf(element.getStringValue());
    else if (type.equals(Boolean.TYPE))
      return Boolean.parseBoolean(element.getStringValue());

    throw new ConversionException(String.format(
      "Parameter [%s] cannot be converted to type [%s].",
      element.getStringValue(), type));
  }

  public ConversionScore conversionScore(Class cls)
  {
    if (cls.equals(Boolean.class) || cls.equals(Boolean.TYPE))
      return ConversionScore.exact;

    return ConversionScore.nomatch;
  }
}
