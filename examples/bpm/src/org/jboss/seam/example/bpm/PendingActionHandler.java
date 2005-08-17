/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.example.bpm;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
public class PendingActionHandler implements ActionHandler
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -462149624982736340L;

   public void execute(ExecutionContext arg0) throws Exception
   {
      System.out.println("Going to pending state");
   }

}


