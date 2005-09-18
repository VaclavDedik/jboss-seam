package org.jboss.seam.example.bpm;

import javax.ejb.Local;

/**
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 */
@Local
public interface MyDocuments
{
   public String find();
   public String select();
   public void destroy();
}
