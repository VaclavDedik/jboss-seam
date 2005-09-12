// $Id$
package org.jboss.seam.example.bpm;

import javax.ejb.Local;

/**
 * Type definition of ProcessMaintenance.
 *
 * @author Steve Ebersole
 */
@Local
public interface ProcessMaintenance
{
   public String cancelProcess();
}
