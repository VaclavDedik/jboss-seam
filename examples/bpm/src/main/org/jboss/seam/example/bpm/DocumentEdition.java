package org.jboss.seam.example.bpm;

import javax.ejb.Local;

/**
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 */
@Local
public interface DocumentEdition
{
   public String save() throws Exception;

   public String approve() throws Exception;
   public String reject() throws Exception;
}
