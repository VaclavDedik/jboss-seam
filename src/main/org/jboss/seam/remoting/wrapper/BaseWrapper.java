package org.jboss.seam.remoting.wrapper;

import java.io.IOException;
import java.io.OutputStream;

import org.dom4j.Element;
import org.jboss.seam.remoting.CallContext;

/**
 *
 * @author Shane Bryzak
 */
public abstract class BaseWrapper implements Wrapper
{
  /**
   *
   */
  protected CallContext context;

  /**
   *
   */
  protected Element element;

  /**
   *
   */
  protected Object value;

  /**
   *
   * @param value Object
   */
  public void setValue(Object value)
  {
    this.value = value;
  }

  /**
   *
   * @return Object
   */
  public Object getValue()
  {
    return value;
  }

  /**
   *
   * @param call Call
   */
  public void setCallContext(CallContext context)
  {
    this.context = context;
  }

  /**
   * Extracts a value from a DOM4J Element
   *
   * @param element Element
   */
  public void setElement(Element element)
  {
    this.element = element;
  }

  public void unmarshal() {}

  public void serialize(OutputStream out) throws IOException { }
}
