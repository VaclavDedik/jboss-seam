package org.jboss.seam.example.issues;

import javax.ejb.Local;

@Local
public interface IssueProjectSelector extends ProjectSelector {
   public String cancel();
   public String selectProject();

}
