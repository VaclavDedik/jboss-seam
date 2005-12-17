/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.util;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;

public final class NamingHelper {
   
    private static final Logger log = Logger.getLogger(NamingHelper.class);
        
    @SuppressWarnings("unchecked")
    public static InitialContext getInitialContext() throws NamingException {
       Map<String, String> properties = (Map<String, String>) Contexts.getApplicationContext().get(Component.PROPERTIES);
       return getInitialContext(properties);
    }

    public static InitialContext getInitialContext(Map<String, String> props) throws NamingException {

        Hashtable<String, String> hash = new Hashtable<String, String>();
        hash.putAll(props);
        log.debug("JNDI InitialContext properties:" + hash);
        try {
            return hash.size()==0 ?
                    new InitialContext() :
                    new InitialContext(hash);
        }
        catch (NamingException e) {
            log.error("Could not obtain initial context", e);
            throw e;
        }
    }

    private NamingHelper() {}

}

