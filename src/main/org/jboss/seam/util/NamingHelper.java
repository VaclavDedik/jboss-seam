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

import org.jboss.logging.Logger;

public final class NamingHelper {
   
    private static final Logger log = Logger.getLogger(NamingHelper.class);
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

    private NamingHelper() {}
    
    public static void setInitialContextProperties(Hashtable initialContextProperties) {
       NamingHelper.initialContextProperties = initialContextProperties;
    }

    public static Hashtable getInitialContextProperties() {
       return initialContextProperties;
    }

}

