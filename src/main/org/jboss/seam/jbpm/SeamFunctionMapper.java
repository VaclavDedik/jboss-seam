package org.jboss.seam.jbpm;

import java.lang.reflect.Method;

import org.jboss.seam.jsf.SeamELFunctionMapper;
import org.jbpm.jpdl.el.FunctionMapper;

/**
 * Wrapper for SeamELFunctionMapper that works with JBPM.
 * 
 * @author Shane Bryzak
 */
public class SeamFunctionMapper implements FunctionMapper
{
   private SeamELFunctionMapper mapper;
   
   public SeamFunctionMapper()
   {
      mapper = new SeamELFunctionMapper();
   }
   
   public Method resolveFunction(String prefix, String localName)
   {
      return mapper.resolveFunction(prefix, localName);
   }
}
