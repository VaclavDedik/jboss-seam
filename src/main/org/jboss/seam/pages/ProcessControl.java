/**
 * 
 */
package org.jboss.seam.pages;

import org.jboss.seam.core.BusinessProcess;
import org.jboss.seam.core.Expressions.ValueBinding;

public class ProcessControl
{
   private boolean isCreateProcess;
   private boolean isResumeProcess;
   private String definition;
   private ValueBinding<Long> processId;

   public void createOrResumeProcess()
   {
      if (createProcess())
      {
         BusinessProcess.instance().createProcess(definition);
      }
      if (resumeProcess())
      {
         BusinessProcess.instance().resumeProcess(processId.getValue());
      }
   }

   private boolean createProcess()
   {
      return isCreateProcess;
   }

   private boolean resumeProcess()
   {
      return isResumeProcess;
   }

   public boolean isCreateProcess()
   {
      return isCreateProcess;
   }
   
   public void setCreateProcess(boolean isCreateProcess)
   {
      this.isCreateProcess = isCreateProcess;
   }
   
   public boolean isResumeProcess()
   {
      return isResumeProcess;
   }
   
   public void setResumeProcess(boolean isResumeProcess)
   {
      this.isResumeProcess = isResumeProcess;
   }
   
   public String getDefinition()
   {
      return definition;
   }
   
   public void setDefinition(String definition)
   {
      this.definition = definition;
   }
   
   public ValueBinding<Long> getProcessId()
   {
      return processId;
   }
   
   public void setProcessId(ValueBinding<Long> processId)
   {
      this.processId = processId;
   }

}