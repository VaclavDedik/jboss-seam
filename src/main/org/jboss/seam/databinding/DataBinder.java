package org.jboss.seam.databinding;

import org.jboss.seam.ScopeType;

/**
 * Allows some "bound type" to be exposed to 
 * the user interface via a "wrapper type".
 * 
 * @author Gavin King
 *
 * @param <Out> the annotation type
 * @param <Type> the bound type
 * @param <WrapperType> the wrapper type
 */
public interface DataBinder<Out, Type, WrapperType>
{
   String getVariableName(Out out);
   ScopeType getVariableScope(Out out);
   WrapperType wrap(Type value);
   Type getWrappedData(WrapperType wrapper);
   Object getSelection(WrapperType wrapper);
   boolean isDirty(WrapperType wrapper, Type value);
}
