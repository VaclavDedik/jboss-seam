package org.jboss.seam.example.bpm;

import javax.ejb.Local;

/**
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 */
@Local
public interface DocumentView
{
   public String details();
   public String save();

   public String approve();
   public String reject();
}
