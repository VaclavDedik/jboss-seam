//$Id$
package org.jboss.seam.finders;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.jboss.seam.annotations.In;

/**
 * Finds objects for injection
 * 
 * @author Gavin King
 */
public interface Finder
{
   String toName(In in, Method method);
   String toName(In in, Field field);
   Object find(In in, String name, Object bean);
}
