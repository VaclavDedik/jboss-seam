package org.jboss.seam.ui;

import javax.faces.component.UIParameter;
import javax.faces.el.ValueBinding;

import org.jbpm.taskmgmt.exe.TaskInstance;

public class UITaskId extends UIParameter
{
   
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UITaskId";
   
   @Override
   public String getName()
   {
      return "taskId";
   }
   
   @Override
   public Object getValue()
   {
      ValueBinding valueBinding = getValueBinding("taskInstance");
      if (valueBinding==null) valueBinding = getFacesContext().getApplication().createValueBinding("#{task}");
      TaskInstance taskInstance = (TaskInstance) valueBinding.getValue( getFacesContext() );
      return taskInstance==null ? null : taskInstance.getId();
   }

}
