package org.jboss.seam.example.bpm;

import javax.ejb.Local;

/**
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 */
@Local
public interface DocumentTaskList
{
   public String find() throws Exception;

   public String selectTask();
   public String nextTask();
   public String previousTask();
}
