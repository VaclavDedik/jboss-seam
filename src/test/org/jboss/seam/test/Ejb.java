//$Id$
package org.jboss.seam.test;

import javax.ejb.Local;

@Local
public interface Ejb
{
   public void foo();
   public void destroy();
}
