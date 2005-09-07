// $Id$
package org.jboss.seam.test.bpm;

import javax.ejb.Stateless;
import javax.ejb.Interceptor;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.ResumeProcess;
import org.jboss.seam.ejb.SeamInterceptor;
import org.jboss.seam.ScopeType;
import org.jboss.annotation.ejb.LocalBinding;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Implementation of ProcessMaintenanceBean.
 *
 * @author Steve Ebersole
 */
@Stateless
@LocalBinding( jndiBinding = "processMaintenance" )
@Interceptor( SeamInterceptor.class )
@Name( "processMaintenance" )
@Scope( ScopeType.STATELESS )
public class ProcessMaintenanceBean implements ProcessMaintenance
{
   @In( value = "process" )
   private ProcessInstance process;

   // illustrates that BusinessProcessInterceptor is applied *before* BijectionInterceptor
   @ResumeProcess( processIdName = "processId", processName = "process" )
   public String cancelProcess()
   {
      assert process != null;
      process.end();
      return "cancelled";
   }
}
