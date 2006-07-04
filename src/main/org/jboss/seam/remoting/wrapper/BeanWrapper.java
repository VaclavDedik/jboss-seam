package org.jboss.seam.remoting.wrapper;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import org.dom4j.Element;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.remoting.InterfaceGenerator;

/**
 * @author Shane Bryzak
 */
public class BeanWrapper extends BaseWrapper implements Wrapper
{
  private static final byte[] REF_START_TAG_OPEN = "<ref id=\"".getBytes();
  private static final byte[] REF_START_TAG_END = "\"/>".getBytes();

  private static final byte[] BEAN_START_TAG_OPEN = "<bean type=\"".getBytes();
  private static final byte[] BEAN_START_TAG_CLOSE = "\">".getBytes();
  private static final byte[] BEAN_CLOSE_TAG = "</bean>".getBytes();

  private static final byte[] MEMBER_START_TAG_OPEN = "<member name=\"".getBytes();
  private static final byte[] MEMBER_START_TAG_CLOSE = "\">".getBytes();
  private static final byte[] MEMBER_CLOSE_TAG = "</member>".getBytes();

  /**
   *
   * @param element Element
   */
  public void setElement(Element element)
  {
    super.setElement(element);

    String beanType = element.attributeValue("type");

    Component component = Component.forName(beanType);

    if (component != null)
      value = Component.newInstance(beanType);
    else
    {
      try {
        value = Class.forName(beanType).newInstance();
      }
      catch (Exception ex) {
        throw new RuntimeException("Could not unmarshal bean element: " + element.getText(), ex);
      }
    }
  }

  /**
   *
   */
  public void unmarshal()
  {
    List members = element.elements("member");

    for (Element member : (List<Element>) members)
    {
      String name = member.attributeValue("name");

      Wrapper w = context.createWrapperFromElement((Element) member.elementIterator().next());

      Class cls = value.getClass();

      Field field = null;

      while (field == null && !cls.equals(Object.class))
      {
        try
        {
          // First check the declared fields
          field = cls.getDeclaredField(name);
        }
        catch (NoSuchFieldException ex)
        {
          // Couldn't find the field.. try the superclass
          cls = cls.getSuperclass();
        }
      }

      if (field == null)
        throw new RuntimeException(String.format(
          "Error while unmarshalling - field [%s] not found in class [%s]",
          name, value.getClass().getName()));

      Object fieldValue = null;
      try {
        fieldValue = w.convert(field.getGenericType());
      }
      catch (ConversionException ex) {
        throw new RuntimeException("Could not convert value while unmarshaling", ex);
      }

      boolean accessible = field.isAccessible();
      try
      {
        if (!accessible)
          field.setAccessible(true);
        field.set(value, fieldValue);
      }
      catch (Exception ex)
      {
        throw new RuntimeException("Could not set field value.", ex);
      }
      finally
      {
        field.setAccessible(accessible);
      }
    }
  }

  /**
   *
   * @param cls Class
   * @return Object
   */
  public Object convert(Type type)
      throws ConversionException
  {
    if (type instanceof Class && ((Class) type).isAssignableFrom(value.getClass()))
      return value;
    else
      throw new ConversionException(String.format(
        "Value [%s] cannot be converted to type [%s].", value, type));
  }

  /**
   *
   * @param out OutputStream
   * @throws IOException
   */
  public void marshal(OutputStream out)
    throws IOException
  {
    context.addOutRef(this);

    out.write(REF_START_TAG_OPEN);
    out.write(Integer.toString(context.getOutRefs().indexOf(this)).getBytes());
    out.write(REF_START_TAG_END);
  }

  /**
   *
   * @param out OutputStream
   * @throws IOException
   */
  public void serialize(OutputStream out)
      throws IOException
  {
    serialize(out, null);
  }

  /**
   *
   * @param out OutputStream
   * @throws IOException
   */
  public void serialize(OutputStream out, List<String> constraints)
    throws IOException
  {
    out.write(BEAN_START_TAG_OPEN);

    Class cls = value.getClass();

    /** @todo This is a hack to get the "real" class - find out if there is
              an API method in CGLIB that can be used instead */
    if (cls.getName().contains("EnhancerByCGLIB"))
      cls = cls.getSuperclass();

    String componentName = Seam.getComponentName(cls);

    if (componentName != null)
      out.write(componentName.getBytes());
    else
      out.write(cls.getName().getBytes());

    out.write(BEAN_START_TAG_CLOSE);

    for (Field f : InterfaceGenerator.getAccessibleFields(cls))
    {
      String fieldPath = path != null && path.length() > 0 ? String.format("%s.%s", path, f.getName()) : f.getName();

      // Also exclude fields listed using wildcard notation: [componentName].fieldName
      String wildCard = String.format("[%s].%s", componentName != null ? componentName : cls.getName(), f.getName());

      if (constraints == null || (!constraints.contains(fieldPath) && !constraints.contains(wildCard)))
      {
        out.write(MEMBER_START_TAG_OPEN);
        out.write(f.getName().getBytes());
        out.write(MEMBER_START_TAG_CLOSE);

        boolean accessible = f.isAccessible();
        try
        {
          // Temporarily set the field's accessibility so we can read it
          f.setAccessible(true);

          context.createWrapperFromObject(f.get(value), fieldPath).marshal(out);
        }
        catch (IllegalAccessException ex)
        {
          throw new RuntimeException("Error reading value from field.");
        }
        finally
        {
          f.setAccessible(accessible);
        }

        out.write(MEMBER_CLOSE_TAG);
      }
    }

    out.write(BEAN_CLOSE_TAG);
  }

  /**
   *
   * @param cls Class
   * @return int
   */
  public ConversionScore conversionScore(Class cls) {
    if (cls.equals(value.getClass()))
      return ConversionScore.exact;
    else if (cls.isAssignableFrom(value.getClass()) || cls.equals(Object.class))
      return ConversionScore.compatible;
    else
      return ConversionScore.nomatch;
  }
}
