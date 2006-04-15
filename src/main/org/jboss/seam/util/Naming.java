/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.util;

import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Naming {
   
   private static final Log log = LogFactory.getLog(Naming.class);
    private static Hashtable initialContextProperties;

    public static InitialContext getInitialContext(Hashtable<String, String> props) throws NamingException {

        log.debug("JNDI InitialContext properties:" + props);
        try {
            return props.size()==0 ?
                    new InitialContext() :
                    new InitialContext(props);
        }
        catch (NamingException e) {
            log.error("Could not obtain initial context", e);
            throw e;
        }
    }
    
    public static InitialContext getInitialContext() throws NamingException {
       return getInitialContext(initialContextProperties);
    }

    private Naming() {}
    
    public static void setInitialContextProperties(Hashtable initialContextProperties) {
       Naming.initialContextProperties = initialContextProperties;
    }

    public static Hashtable getInitialContextProperties() {
       return initialContextProperties;
    }

}

