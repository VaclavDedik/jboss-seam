package org.jboss.seam.microcontainer;

import java.util.Hashtable;

import javax.naming.NamingException;

import org.jboss.seam.init.Initialization;

public class JndiPropertiesFactory {
   public Hashtable getJndiProperties() throws NamingException {
      Hashtable hash = new Hashtable();
      Initialization.loadFromResource(hash, "/seam-jndi.properties");
      return hash;
   }
}
