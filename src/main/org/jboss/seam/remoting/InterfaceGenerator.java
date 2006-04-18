package org.jboss.seam.remoting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.PhaseId;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.ComponentType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.WebRemote;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.annotations.Name;

/**
 * Generates JavaScript interface code.
 *
 * @author Shane Bryzak
 */
public class InterfaceGenerator extends BaseRequestHandler implements RequestHandler
{
   private static final Log log = LogFactory.getLog(InterfaceGenerator.class);

  /**
   * Maintain a cache of the accessible fields
   */
  private static Map<Class,List<Field>> accessibleFields = new HashMap<Class,List<Field>>();

  /**
   * A cache of component interfaces, keyed by component name.
   */
  private Map<String,byte[]> interfaceCache = new HashMap<String,byte[]>();

  private ServletContext servletContext;

  public void setServletContext(ServletContext ctx)
  {
    this.servletContext = ctx;
  }

  /**
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @throws Exception
   */
  public void handle(HttpServletRequest request, HttpServletResponse response)
      throws Exception
  {
    try
    {
      HttpSession session = ( (HttpServletRequest) request).getSession(true);
      Lifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
      Lifecycle.setServletRequest(request);
      Lifecycle.beginRequest(servletContext, session);

      String[] componentNames = request.getQueryString().split("&");
      Component[] components = new Component[componentNames.length];

      for (int i = 0; i < componentNames.length; i++) {
        components[i] = Component.forName(componentNames[i]);
        if (components[i] == null) {
          log.error(String.format("Component not found: [%s]", componentNames[i]));
          throw new ServletException("Invalid request - component not found.");
        }
      }

      generateComponentInterface(components, response.getOutputStream());
    }
    finally
    {
      Lifecycle.setServletRequest(null);
      Lifecycle.setPhaseId(null);
    }
  }

  /**
   * Generates the JavaScript code required to invoke the methods of a component/s.
   *
   * @param components Component[] The components to generate javascript for
   * @param out OutputStream The OutputStream to write the generated javascript to
   * @throws IOException Thrown if there is an error writing to the OutputStream
   */
  public void generateComponentInterface(Component[] components, OutputStream out)
      throws IOException
  {
    Set<Type> types = new HashSet<Type>();
    for (Component c : components)
    {
      if (!interfaceCache.containsKey(c.getName()))
      {
        synchronized(interfaceCache)
        {
          if (!interfaceCache.containsKey(c.getName()))
          {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            appendComponentSource(bOut, c, types);
            interfaceCache.put(c.getName(), bOut.toByteArray());
          }
        }
      }
      out.write(interfaceCache.get(c.getName()));
    }
  }

  /**
   * A helper method, used internally by InterfaceGenerator and also when
   * serializing responses.  Returns a list of the fields for the specified type
   * which should be included in the generated interface for the type.
   *
   * @param cls Class
   * @return List
   */
  public static List<Field> getAccessibleFields(Class cls)
  {
    /** @todo This is a hack to get the "real" class - find out if there is
              an API method in CGLIB that can be used instead */
    if (cls.getName().contains("EnhancerByCGLIB"))
      cls = cls.getSuperclass();

    if (!accessibleFields.containsKey(cls))
    {
      synchronized(accessibleFields)
      {
        if (!accessibleFields.containsKey(cls))
        {
          List<Field> fields = new ArrayList<Field>();

          for (Field f : cls.getDeclaredFields())
          {
            if (!Modifier.isTransient(f.getModifiers()) && !Modifier.isStatic(f.getModifiers()))
            {
              String fieldName = f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);
              String getterName = String.format("get%s", fieldName);
              String setterName = String.format("set%s", fieldName);
              Method getMethod = null;
              Method setMethod = null;

              try {
                getMethod = cls.getMethod(getterName);
              }
              catch (SecurityException ex) { }
              catch (NoSuchMethodException ex)
              {
                // it might be an "is" method...
                getterName = String.format("is%s", fieldName);
                try
                {
                  getMethod = cls.getMethod(getterName);
                }
                catch (NoSuchMethodException ex2) { /* don't care */ }
              }

              try {
                setMethod = cls.getMethod(setterName, new Class[] {f.getType()});
              }
              catch (SecurityException ex) { }
              catch (NoSuchMethodException ex) { /* don't care */ }

              if (Modifier.isPublic(f.getModifiers()) ||
                  (getMethod != null && Modifier.isPublic(getMethod.getModifiers()) ||
                  (setMethod != null && Modifier.isPublic(setMethod.getModifiers()))))
              {
                fields.add(f);
              }
            }
          }

          accessibleFields.put(cls, fields);
        }
      }
    }

    return accessibleFields.get(cls);
  }

  /**
   * Appends component interface code to an outputstream for a specified component.
   *
   * @param out OutputStream The OutputStream to write to
   * @param component Component The component to generate an interface for
   * @param types Set A list of types that have already been generated for this
   * request.  If this component has already been generated (i.e. it is in the list)
   * then it won't be generated again
   * @throws IOException If there is an error writing to the OutputStream.
   */
  private void appendComponentSource(OutputStream out, Component component, Set<Type> types)
      throws IOException
  {
    StringBuilder componentSrc = new StringBuilder();

    Class type = null;

    if ((component.getType().equals(ComponentType.STATEFUL_SESSION_BEAN) ||
        component.getType().equals(ComponentType.STATELESS_SESSION_BEAN)) &&
        component.getLocalInterfaces().size() > 0)
    {
      type = component.getLocalInterfaces().iterator().next();
    }
    else if (component.getType().equals(ComponentType.ENTITY_BEAN))
    {
      appendTypeSource(out, component.getBeanClass(), types);
      return;
    }
    else if (component.getType().equals(ComponentType.JAVA_BEAN))
    {
      // Check if any of the methods are annotated with @WebRemote, and if so
      // treat it as an "action" component instead of a type component
      for (Method m : component.getBeanClass().getDeclaredMethods())
      {
        if (m.getAnnotation(WebRemote.class) != null)
        {
          type = component.getBeanClass();
          break;
        }
      }

      if (type == null)
      {
        appendTypeSource(out, component.getBeanClass(), types);
        return;
      }
    }
    else
      type = component.getBeanClass();

    if (types.contains(type))
      return;

    types.add(type);

    componentSrc.append("Seam.Remoting.type.");
    componentSrc.append(component.getName());
    componentSrc.append(" = function() {\n");
    componentSrc.append("  this.__callback = new Object();\n");

    for (Method m : type.getDeclaredMethods())
    {
      if (m.getAnnotation(WebRemote.class) == null)
        continue;

      // Append the return type to the source block
      appendTypeSource(out, m.getGenericReturnType(), types);

      componentSrc.append("  Seam.Remoting.type.");
      componentSrc.append(component.getName());
      componentSrc.append(".prototype.");
      componentSrc.append(m.getName());
      componentSrc.append(" = function(");

      // Insert parameters p0..pN
      for (int i = 0; i < m.getGenericParameterTypes().length; i++)
      {
        appendTypeSource(out, m.getGenericParameterTypes()[i], types);

        if (i > 0)
          componentSrc.append(", ");
        componentSrc.append("p");
        componentSrc.append(i);
      }

      if (m.getGenericParameterTypes().length > 0)
        componentSrc.append(", ");
      componentSrc.append("callback) {\n");

      componentSrc.append("    Seam.Remoting.execute(this, \"");
      componentSrc.append(m.getName());
      componentSrc.append("\", [");

      for (int i = 0; i < m.getParameterTypes().length; i++)
      {
        if (i > 0)
          componentSrc.append(", ");
        componentSrc.append("p");
        componentSrc.append(i);
      }

      componentSrc.append("], callback);\n");

      componentSrc.append("  }\n");
    }

    componentSrc.append("}\n");

    // Set the component name
    componentSrc.append("Seam.Remoting.type.");
    componentSrc.append(component.getName());
    componentSrc.append(".__name = \"");
    componentSrc.append(component.getName());
    componentSrc.append("\";\n\n");

    // Register the component
    componentSrc.append("Seam.Component.register(Seam.Remoting.type.");
    componentSrc.append(component.getName());
    componentSrc.append(");\n\n");

    out.write(componentSrc.toString().getBytes());
  }

  /**
   * Append Javascript interface code for a specified class to a block of code.
   *
   * @param source StringBuilder The code block to append to
   * @param type Class The type to generate a Javascript interface for
   * @param types Set A list of the types already generated (only include each type once).
   */
  private void appendTypeSource(OutputStream out, Type type, Set<Type> types)
      throws IOException
  {
    if (type instanceof Class)
    {
      Class classType = (Class) type;

      if (classType.isArray())
      {
        appendTypeSource(out, classType.getComponentType(), types);
        return;
      }

      if (classType.getName().startsWith("java.") ||
          types.contains(type) || classType.isPrimitive())
      return;

      // Keep track of which types we've already added
      types.add(type);

      appendClassSource(out, classType, types);
    }
    else if (type instanceof ParameterizedType)
    {
      for (Type t : ((ParameterizedType) type).getActualTypeArguments())
        appendTypeSource(out, t, types);
    }
  }

  /**
   * Appends the interface code for a non-component class to an OutputStream.
   *
   * @param out OutputStream
   * @param classType Class
   * @param types Set
   * @throws IOException
   */
  private void appendClassSource(OutputStream out, Class classType, Set<Type> types)
      throws IOException
  {
    // Don't generate interfaces for enums
    if (classType.isEnum())
      return;

    StringBuilder typeSource = new StringBuilder();

    // Determine whether this class is a component; if so, use its name
    // otherwise use its class name.
    String componentName = Seam.getComponentName(classType);
    if (componentName == null)
      componentName = classType.getName();

    String typeName = componentName.replace('.', '$');

    typeSource.append("Seam.Remoting.type.");
    typeSource.append(typeName);
    typeSource.append(" = function() {\n");

    StringBuilder fields = new StringBuilder();
    StringBuilder accessors = new StringBuilder();
    StringBuilder mutators = new StringBuilder();
    Map<String,String> metadata = new HashMap<String,String>();

    for (Field f : getAccessibleFields(classType))
    {
      appendTypeSource(out, f.getType(), types);

      // Include types referenced by generic declarations
      if (f.getGenericType() instanceof ParameterizedType)
      {
        for (Type t : ((ParameterizedType) f.getGenericType()).getActualTypeArguments())
        {
          if (t instanceof Class)
            appendTypeSource(out, (Class) t, types);
        }
      }

      String fieldName = f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);
      String getterName = String.format("get%s", fieldName);
      String setterName = String.format("set%s", fieldName);
      Method getMethod = null;
      Method setMethod = null;

      try {
        getMethod = classType.getMethod(getterName);
      }
      catch (SecurityException ex) { }
      catch (NoSuchMethodException ex)
      {
        getterName = String.format("is%s", fieldName);
        try
        {
          getMethod = classType.getMethod(getterName);
        }
        catch (NoSuchMethodException ex2) { /* don't care */ }
      }

      try {
        setMethod = classType.getMethod(setterName, new Class[] {f.getType()});
      }
      catch (SecurityException ex) { }
      catch (NoSuchMethodException ex) { /* don't care */ }

      // Construct the list of fields.  Only include fields that are public,
      // or have a getter or setter method that is public
      if (Modifier.isPublic(f.getModifiers()) ||
          (getMethod != null && Modifier.isPublic(getMethod.getModifiers()) ||
          (setMethod != null && Modifier.isPublic(setMethod.getModifiers()))))
      {
        metadata.put(f.getName(), getFieldType(f.getType()));

        fields.append("  this.");
        fields.append(f.getName());
        fields.append(" = null;\n");

        if (getMethod != null)
        {
          accessors.append("  Seam.Remoting.type.");
          accessors.append(typeName);
          accessors.append(".prototype.");
          accessors.append(getMethod.getName());
          accessors.append(" = function() { return this.");
          accessors.append(f.getName());
          accessors.append("; }\n");
        }

        if (setMethod != null)
        {
          mutators.append("  Seam.Remoting.type.");
          mutators.append(typeName);
          mutators.append(".prototype.");
          mutators.append(setMethod.getName());
          mutators.append(" = function(");
          mutators.append(f.getName());
          mutators.append(") { this.");
          mutators.append(f.getName());
          mutators.append(" = ");
          mutators.append(f.getName());
          mutators.append("; }\n");
        }
      }
    }

    typeSource.append(fields);
    typeSource.append(accessors);
    typeSource.append(mutators);

    typeSource.append("}\n\n");

    // Append the type name
    typeSource.append("Seam.Remoting.type.");
    typeSource.append(typeName);
    typeSource.append(".__name = \"");
    typeSource.append(componentName);
    typeSource.append("\";\n");

    // Append the metadata
    typeSource.append("Seam.Remoting.type.");
    typeSource.append(typeName);
    typeSource.append(".__metadata = [\n");

    for (String key : metadata.keySet())
    {
      typeSource.append("  {field: \"");
      typeSource.append(key);
      typeSource.append("\", type: \"");
      typeSource.append(metadata.get(key));
      typeSource.append("\"},\n");
    }

    typeSource.append("];\n\n");

    // Register the type under Seam.Component if it is a component, otherwise
    // register it under Seam.Remoting

    if (classType.isAnnotationPresent(Name.class))
      typeSource.append("Seam.Component.register(Seam.Remoting.type.");
    else
      typeSource.append("Seam.Remoting.registerType(Seam.Remoting.type.");

    typeSource.append(typeName);
    typeSource.append(");\n\n");

    out.write(typeSource.toString().getBytes());
  }

  /**
   * Returns the remoting "type" for a specified class.
   *
   * @param type Class
   * @return String
   */
  private String getFieldType(Class type)
  {
    if (type.equals(String.class) || type.isEnum())
      return "string";
    else if (type.equals(Boolean.class) || type.equals(Boolean.TYPE))
      return "boolean";
    else if (type.equals(Short.class) || type.equals(Short.TYPE))
      return "short";
    else if (type.equals(Integer.class) || type.equals(Integer.TYPE))
      return "int";
    else if (type.equals(Long.class) || type.equals(Long.TYPE))
      return "long";
    else if (type.equals(Float.class) || type.equals(Float.TYPE))
      return "float";
    else if (type.equals(Double.class) || type.equals(Double.TYPE))
      return "double";
    else if (type.equals(Date.class))
      return "date";
    else if (type.isArray() || Collection.class.isAssignableFrom(type))
      return "bag";
    else
      return "bean";
  }
}
