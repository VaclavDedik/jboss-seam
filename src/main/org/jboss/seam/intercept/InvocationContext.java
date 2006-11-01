package org.jboss.seam.intercept;

import java.lang.reflect.Method;
import java.util.Map;

public interface InvocationContext
{
   public Object getTarget();
   public Map getContextData();
   public Method getMethod();
   public Object[] getParameters();
   public Object proceed() throws Exception;
   public void setParameters(Object[] params);
}
