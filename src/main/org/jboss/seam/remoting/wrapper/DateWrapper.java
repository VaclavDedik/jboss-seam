package org.jboss.seam.remoting.wrapper;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Shane Bryzak
 */
public class DateWrapper extends BaseWrapper implements Wrapper
{
  private static final byte[] DATE_TAG_OPEN = "<date>".getBytes();
  private static final byte[] DATE_TAG_CLOSE = "</date>".getBytes();
  private static final DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");

  public void marshal(OutputStream out) throws IOException
  {
    out.write(DATE_TAG_OPEN);
    out.write(df.format(value).getBytes());
    out.write(DATE_TAG_CLOSE);
  }

  public Object convert(Type type)
      throws ConversionException
  {
    if (type.equals(Date.class))
    {
      try {
        return df.parse(element.getStringValue());
      }
      catch (ParseException ex) {
        throw new RuntimeException(String.format(
            "Date value [%s] is not in a valid format.", element.getStringValue()));
      }
    }
    else
      throw new ConversionException(String.format(
        "Value [%s] cannot be converted to type [%s].", element.getStringValue(),
        type));
  }

  public ConversionScore conversionScore(Class cls)
  {
    if (cls.equals(Date.class))
      return ConversionScore.exact;

    return ConversionScore.nomatch;
  }
}
