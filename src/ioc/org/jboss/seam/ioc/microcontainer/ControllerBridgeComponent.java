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
import javax.servlet.ServletContext;

import org.jboss.beans.metadata.plugins.AbstractBeanMetaData;
import org.jboss.dependency.spi.Controller;
import org.jboss.kernel.plugins.dependency.AbstractKernelControllerContext;
import org.jboss.seam.Component;
import static org.jboss.seam.InterceptionType.NEVER;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Lifecycle;

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
public class ControllerBridgeComponent implements Serializable
{
    /** The serialVersionUID */
    private static final long serialVersionUID = 1L;

    public ServletContext getServletContext()
    {
        return Lifecycle.getServletContext();
    }

    protected Controller getController()
    {
        // todo get underlying Controller from ServletContext
        return null;
    }

    @Create
    public void create(Component component)
    {
        try
        {
            Controller controller = getController();
            AbstractBeanMetaData metaData = new AbstractBeanMetaData(getClass().getSimpleName(), getClass().getName());
            controller.install(new AbstractKernelControllerContext(null, metaData, this));
        }
        catch (Throwable t)
        {
            throw new IllegalArgumentException("Exception installing ControllerBridgeComponent: " + t);
        }
    }

    @Destroy
    public void destroy()
    {
        Controller controller = getController();
        controller.uninstall(getClass().getSimpleName());
    }

}
