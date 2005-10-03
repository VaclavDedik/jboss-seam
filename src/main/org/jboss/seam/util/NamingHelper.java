/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.util;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;

public final class NamingHelper {
   
    public static String JNDI_CLASS = "seam.jndi.class";
    public static String JNDI_URL = "seam.jndi.url";
    public static String JNDI_PREFIX = "seam.jndi";
    
    private static final Logger log = Logger.getLogger(NamingHelper.class);
    
    
    @SuppressWarnings("unchecked")
    public static InitialContext getInitialContext() throws NamingException {
       Map<String, String> properties = (Map<String, String>) Contexts.getApplicationContext().get(Component.PROPERTIES);
       return getInitialContext(properties);
    }

    public static InitialContext getInitialContext(Map<String, String> props) throws NamingException {

        Hashtable hash = getJndiProperties(props);
        log.info("JNDI InitialContext properties:" + hash);
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

    /**
     * Transform JNDI properties passed in the form <tt>hibernate.jndi.*</tt> to the
     * format accepted by <tt>InitialContext</tt> by triming the leading "<tt>seam.jndi</tt>".
     */
    public static Properties getJndiProperties(Map<String, String> properties) {

        HashSet<String> specialProps = new HashSet<String>();
        specialProps.add(JNDI_CLASS);
        specialProps.add(JNDI_URL);

        Iterator iter = properties.keySet().iterator();
        Properties result = new Properties();
        while ( iter.hasNext() ) {
            String prop = (String) iter.next();
            if ( prop.indexOf(JNDI_PREFIX) > -1 && !specialProps.contains(prop) ) {
                result.setProperty(
                        prop.substring(JNDI_PREFIX.length()+1 ),
                        properties.get(prop)
                    );
            }
        }

        String jndiClass = properties.get(JNDI_CLASS);
        String jndiURL = properties.get(JNDI_URL);
        // we want to be able to just use the defaults,
        // if JNDI environment properties are not supplied
        // so don't put null in anywhere
        if (jndiClass != null) result.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, jndiClass);
        if (jndiURL != null) result.put(javax.naming.Context.PROVIDER_URL, jndiURL);

        return result;
    }

    private NamingHelper() {}

}

