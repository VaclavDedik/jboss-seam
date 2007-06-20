package org.jboss.seam.bpm;

import java.lang.reflect.Method;

import org.jbpm.jpdl.el.FunctionMapper;

/**
 * Wrapper for SeamFunctionMapper that works with JBPM.
 * 
 * @author Shane Bryzak
 */
class SeamFunctionMapper implements FunctionMapper
{
   private org.jboss.seam.el.SeamFunctionMapper mapper;
   
   public SeamFunctionMapper()
   {
      mapper = new org.jboss.seam.el.SeamFunctionMapper();
   }
   
   public Method resolveFunction(String prefix, String localName)
   {
      return mapper.resolveFunction(prefix, localName);
   }
}
