// $Id$
package org.jboss.seam.example.bpm;

import javax.ejb.Stateless;
import javax.ejb.Interceptor;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.ResumeProcess;
import org.jboss.seam.ejb.SeamInterceptor;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Implementation of ProcessMaintenanceBean.
 *
 * @author Steve Ebersole
 */
@Stateless
@Name( "processMaintenance" )
@Interceptor( SeamInterceptor.class )
public class ProcessMaintenanceBean implements ProcessMaintenance
{
   @In( value = "process" )
   private ProcessInstance process;

   // demonstrates that BusinessProcessInterceptor is applied *before* BijectionInterceptor
   @ResumeProcess( processIdName = "processId", processName = "process" )
   public String cancelProcess()
   {
      assert process != null;
      process.end();
      return "cancelled";
   }
}
