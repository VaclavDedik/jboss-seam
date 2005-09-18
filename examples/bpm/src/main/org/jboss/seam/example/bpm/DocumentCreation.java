package org.jboss.seam.example.bpm;

import javax.ejb.Local;

/**
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 */
@Local
public interface DocumentCreation
{
   public String start();
   public String create();
   public String save();
}
