package org.jboss.seam.example.bpm;

import javax.ejb.Local;
// $Id$

@Local
public interface ApprovalHandler
{
   public String beginApproval();
   public String approve();
   public String deny();
}
