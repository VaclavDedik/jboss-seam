package org.jboss.seam.test.bpm;

import javax.ejb.Local;
// $Id$

@Local
public interface ApprovalHandler
{
   public String beginApproval();
   public String approve();
   public String deny();
}
