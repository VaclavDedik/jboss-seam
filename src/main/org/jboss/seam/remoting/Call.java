package org.jboss.seam.remoting;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Constrain;
import org.jboss.seam.annotations.WebRemote;
import org.jboss.seam.remoting.wrapper.ConversionException;
import org.jboss.seam.remoting.wrapper.ConversionScore;
import org.jboss.seam.remoting.wrapper.Wrapper;

/**
 *
 * @author Shane Bryzak
 */
public class Call
{
  private String id;
  private String componentName;
  private String methodName;

  private List<Wrapper> params = new ArrayList<Wrapper> ();

  private Object result;

  private CallContext context;

  private List<String> constraints = null;

  /**
   * Constructor.
   *
   * @param componentName String
   * @param methodName String
   */
  public Call(String id, String componentName, String methodName)
  {
    this.id = id;
    this.componentName = componentName;
    this.methodName = methodName;
    this.context = new CallContext();
  }

  /**
   * Return the call context.
   *
   * @return CallContext
   */
  public CallContext getContext()
  {
    return context;
  }

  /**
   * Add a parameter to this call.
   *
   * @param param BaseWrapper
   */
  public void addParameter(Wrapper param)
  {
    params.add(param);
  }

  /**
   * Returns the result of this call.
   *
   * @return Wrapper
   */
  public Object getResult()
  {
    return result;
  }

  /**
   * Returns the id of this call.
   *
   * @return String
   */
  public String getId()
  {
    return id;
  }

  /**
   * Returns the object graph constraints annotated on the method that is called.
   *
   * @return List The constraints
   */
  public List<String> getConstraints()
  {
    return constraints;
  }

  /**
   * Execute this call
   *
   * @throws Exception
   */
  public void execute()
      throws Exception
  {
    // Find the component we're calling
    Component component = Component.forName(componentName);

    if (component == null)
      throw new RuntimeException("No such component: " + componentName);

    // Create an instance of the component
    Object instance = Component.getInstance(componentName, true);

    Class type = null;

    if (component.getBusinessInterfaces().size() > 0)
    {
      // Get the local interface for the component - this is the type that we're
      // going to assume we're invoking against.
      type = component.getBusinessInterfaces().iterator().next();
    }

    if (type == null)
      type = component.getBeanClass();

    // Find the method according to the method name and the parameter classes
    Method m = findMethod(methodName, type);
    if (m == null)
      throw new RuntimeException("No compatible method found.");

    if (m.getAnnotation(Constrain.class) != null)
      constraints = Arrays.asList(m.getAnnotation(Constrain.class).value());

    // Invoke!
    result = m.invoke(instance, convertParams(m.getGenericParameterTypes()));
  }

  /**
   * Convert our parameter values to an Object array of the specified target
   * types.
   *
   * @param targetTypes Class[] An array containing the target class types.
   * @return Object[] The converted parameter values.
   */
  private Object[] convertParams(Type[] targetTypes)
      throws ConversionException
  {
    Object[] paramValues = new Object[targetTypes.length];

    for (int i = 0; i < targetTypes.length; i++)
      paramValues[i] = params.get(i).convert(targetTypes[i]);

    return paramValues;
  }

  /**
   * Find the best matching method within the specified class according to
   * the parameter types that were provided to the Call.
   *
   * @param name String The name of the method.
   * @param cls Class The Class to search in.
   * @return Method The best matching method.
   */
  private Method findMethod(String name, Class cls)
  {
    Map<Method, Integer> candidates = new HashMap<Method, Integer> ();

    for (Method m : cls.getDeclaredMethods()) {
      if (m.getAnnotation(WebRemote.class) == null)
        continue;

      if (name.equals(m.getName()) &&
          m.getParameterTypes().length == params.size()) {
        int score = 0;

        for (int i = 0; i < m.getParameterTypes().length; i++) {
          ConversionScore convScore = params.get(i).conversionScore(m.getParameterTypes()[
              i]);
          if (convScore == ConversionScore.nomatch)
            continue;
          score += convScore.getScore();
        }
        candidates.put(m, score);
      }
    }

    Method bestMethod = null;
    int bestScore = 0;

    for (Method m : candidates.keySet()) {
      int thisScore = candidates.get(m).intValue();
      if (bestMethod == null || thisScore > bestScore) {
        bestMethod = m;
        bestScore = thisScore;
      }
    }

    return bestMethod;
  }
}
