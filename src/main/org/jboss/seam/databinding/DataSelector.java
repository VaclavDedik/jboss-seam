package org.jboss.seam.databinding;

/**
 * Allows extraction of the selected item
 * from some "wrapper type".
 * 
 * @author Gavin King
 *
 * @param <In> the annotation type
 * @param <WrapperType> the wrapper type
 */
public interface DataSelector<In, WrapperType>
{
   String getVariableName(In in);
   Object getSelection(WrapperType wrapper);
}
