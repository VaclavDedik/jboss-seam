package org.jboss.seam.databinding;


public interface DataSelector<In, WrapperType>
{
   String getVariableName(In in);
   Object getSelection(WrapperType wrapper);
}
