/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.seam.ioc.microcontainer;

import java.io.Serializable;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.jboss.mx.util.MBeanProxyExt;
import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.seam.Component;
import static org.jboss.seam.InterceptionType.NEVER;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.log.Log;
import org.jboss.system.ServiceControllerMBean;

/**
 * Notifies Seam components in current underlying Microcontainer Controller.
 * Meaning that ServletContext is available to register MC beans as Seam components
 * and MC beans can lookup Seam components.
 * 
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Install(false)
@Startup
public class ControllerBridgeComponent implements ControllerBridgeComponentMBean, Serializable
{
    /** The serialVersionUID */
    private static final long serialVersionUID = 1L;

    @Logger private Log log;
    private ObjectName objectName;

    protected ObjectName createObjectName(Component component) throws Exception
    {
        return new ObjectName("jboss-seam:name=" + getClass().getSimpleName() + "." + component.getName());
    }

    protected void handleJMXRegistration(boolean register) throws Exception
    {
        MBeanServer server = MBeanServerLocator.locateJBoss();
        if (server == null)
            throw new IllegalArgumentException("Not running in JBoss app. server [currently only supporting]");
        ServiceControllerMBean serviceController = (ServiceControllerMBean)MBeanProxyExt.create(ServiceControllerMBean.class, ServiceControllerMBean.OBJECT_NAME);
        if (register)
        {
            server.registerMBean(this, objectName);
            // we want it to be installed
            serviceController.start(objectName);
        }
        else
        {
            // destroy it
            serviceController.destroy(objectName);
            serviceController.remove(objectName);
            server.unregisterMBean(objectName);
        }
    }

    @Create
    public void create(Component component)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Creating notification MC component ...");
        }
        try
        {
            objectName = createObjectName(component);
            handleJMXRegistration(true);
        }
        catch (Throwable t)
        {
            throw new IllegalArgumentException("Exception installing ControllerBridgeComponent: " + t);
        }
    }

    @Destroy
    public void destroy() throws Exception
    {
        handleJMXRegistration(false);
        objectName = null;
        if (log.isDebugEnabled())
        {
            log.debug("Notification MC component destroyed ...");
        }
    }

}
