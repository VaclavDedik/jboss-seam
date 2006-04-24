package org.jboss.seam.remoting.wrapper;

/**
 * Thrown for an invalid conversion.
 *
 * @author Shane Bryzak
 */
public class ConversionException extends Exception
{
  public ConversionException(String message)
  {
    super(message);
  }

  public ConversionException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
