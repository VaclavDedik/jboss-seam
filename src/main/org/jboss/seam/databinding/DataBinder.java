package org.jboss.seam.databinding;

import org.jboss.seam.ScopeType;

public interface DataBinder<Out, Type, WrapperType>
{
   String getVariableName(Out out);
   ScopeType getVariableScope(Out out);
   WrapperType wrap(Type value);
   Type getWrappedData(WrapperType wrapper);
   Object getSelection(WrapperType wrapper);
}
