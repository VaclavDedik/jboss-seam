package org.jboss.seam.example.remoting;

import javax.ejb.Local;
import org.jboss.seam.annotations.WebRemote;

@Local
public interface HelloLocal {
  @WebRemote
  public String sayHello(String name);
}

