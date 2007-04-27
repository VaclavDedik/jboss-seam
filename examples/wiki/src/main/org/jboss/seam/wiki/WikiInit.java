package org.jboss.seam.wiki;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.ScopeType;
import org.jboss.seam.core.Events;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;
import org.hibernate.util.NamingHelper;
import org.hibernate.jmx.StatisticsService;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.sql.DataSource;
import javax.naming.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.ArrayList;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.ManagementService;

@Name("wikiInit")
@Scope(ScopeType.APPLICATION)
@Startup
public class WikiInit {

    @Logger static Log log;

    private ObjectName hibernateMBeanName;

    @Create
    public void init() throws Exception {
        log.info("Starting LaceWiki...");

        //System.out.println(listJNDITree(""));

        log.info("registering Hibernate statistics MBean");
        hibernateMBeanName = new ObjectName("Hibernate:type=statistics,application=laceWiki");
        StatisticsService mBean = new StatisticsService();
        mBean.setSessionFactoryJNDIName("SessionFactories/laceWikiSF");
        ManagementFactory.getPlatformMBeanServer().registerMBean(mBean, hibernateMBeanName);

    }

    @Destroy
    public void shutdown() throws Exception {
        log.info("Stopping LaceWiki");

        log.info("unregistering Hibernate statistics MBean");
        ManagementFactory.getPlatformMBeanServer().unregisterMBean(hibernateMBeanName);
    }

    /** Utility to debug JBoss JNDI problems */
    public static String listJNDITree(String namespace) {
        StringBuffer buffer = new StringBuffer(4096);
        try {
            Properties props = new Properties();
            Context context = new InitialContext(props); // From jndi.properties
            if (namespace!= null)
                context = (Context) context.lookup(namespace);
            buffer.append("Namespace: " + namespace +"\n");
            buffer.append("#####################################\n");
            list(context, " ", buffer, true);
            buffer.append("#####################################\n");
        }
        catch (NamingException e) {
            buffer.append("Failed to get InitialContext, " + e.toString(true));
        }
        return buffer.toString();
    }

    private static void list(Context ctx, String indent, StringBuffer buffer, boolean verbose) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            NamingEnumeration ne = ctx.list("");
            while (ne.hasMore()) {
                NameClassPair pair = (NameClassPair) ne.next();

                String name = pair.getName();
                String className = pair.getClassName();
                boolean recursive = false;
                boolean isLinkRef = false;
                boolean isProxy = false;
                Class c = null;
                try {
                    c = loader.loadClass(className);

                    if (Context.class.isAssignableFrom(c))
                        recursive = true;
                    if (LinkRef.class.isAssignableFrom(c))
                        isLinkRef = true;

                    isProxy = Proxy.isProxyClass(c);
                }
                catch (ClassNotFoundException cnfe) {
                    // If this is a $Proxy* class its a proxy
                    if (className.startsWith("$Proxy")) {
                        isProxy = true;
                        // We have to get the class from the binding
                        try {
                            Object p = ctx.lookup(name);
                            c = p.getClass();
                        }
                        catch (NamingException e) {
                            Throwable t = e.getRootCause();
                            if (t instanceof ClassNotFoundException) {
                                // Get the class name from the exception msg
                                String msg = t.getMessage();
                                if (msg != null) {
                                    // Reset the class name to the CNFE class
                                    className = msg;
                                }
                            }
                        }
                    }
                }

                buffer.append(indent + " +- " + name);

                // Display reference targets
                if (isLinkRef) {
                    // Get the
                    try {
                        Object obj = ctx.lookupLink(name);

                        LinkRef link = (LinkRef) obj;
                        buffer.append("[link -> ");
                        buffer.append(link.getLinkName());
                        buffer.append(']');
                    }
                    catch (Throwable t) {
                        buffer.append("invalid]");
                    }
                }

                // Display proxy interfaces
                if (isProxy) {
                    buffer.append(" (proxy: " + pair.getClassName());
                    if (c != null) {
                        Class[] ifaces = c.getInterfaces();
                        buffer.append(" implements ");
                        for (int i = 0; i < ifaces.length; i++) {
                            buffer.append(ifaces[i]);
                            buffer.append(',');
                        }
                        buffer.setCharAt(buffer.length() - 1, ')');
                    } else {
                        buffer.append(" implements " + className + ")");
                    }
                } else if (verbose) {
                    buffer.append(" (class: " + pair.getClassName() + ")");
                }

                buffer.append('\n');
                if (recursive) {
                    try {
                        Object value = ctx.lookup(name);
                        if (value instanceof Context) {
                            Context subctx = (Context) value;
                            list(subctx, indent + " |  ", buffer, verbose);
                        } else {
                            buffer.append(indent + " |   NonContext: " + value);
                            buffer.append('\n');
                        }
                    }
                    catch (Throwable t) {
                        buffer.append("Failed to lookup: " + name + ", errmsg=" + t.getMessage());
                        buffer.append('\n');
                    }
                }
            }
            ne.close();
        }
        catch (NamingException ne) {
            buffer.append("error while listing context " + ctx.toString() + ": " + ne.toString(true));
        }
    }



}
