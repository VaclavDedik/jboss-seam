package org.jboss.seam.example.remoting;

import javax.ejb.Local;
import org.jboss.seam.annotations.Remotable;

@Local
public interface HelloLocal {
  @Remotable
  public String sayHello(String name);
}
   