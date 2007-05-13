package org.jboss.seam.ui;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.UIParameter;

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
      ValueExpression ValueExpression = getValueExpression("taskInstance");
      ELContext context = getFacesContext().getELContext();
      if (ValueExpression==null) ValueExpression = getFacesContext().getApplication().getExpressionFactory().createValueExpression(context, "#{task}", TaskInstance.class);
      TaskInstance taskInstance = (TaskInstance) ValueExpression.getValue(context);
      return taskInstance==null ? null : taskInstance.getId();
   }

}
