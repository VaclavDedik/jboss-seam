package org.jboss.seam.example.pageflow;

import javax.ejb.Local;

@Local
public interface SimpleFlow {
   public void begin();
   public String gotoFirstPage();
   public String continueFlowDecision();
   public String gotoSecondPage();
   public String gotoThirdPage();
   public void destroy();
}