package org.jboss.seam.resteasy;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.StringConverter;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.core.Init;
import org.jboss.seam.util.EJB;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.JndiName;
import org.jboss.seam.log.Log;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.Set;

/**
 * An extended version of the RESTEasy dispatcher, configured on Seam application
 * startup with a custom JAX RS <tt>Application</tt> instance. Registers custom resource
 * and provider lifecycle handlers with RESTEasy, depending on configured/detected resources
 * from <tt>ResteasyBootstrap</tt>.
 *
 * @author Christian Bauer
 */
@Name("org.jboss.seam.resteasy.dispatcher")
@Scope(ScopeType.APPLICATION)
@Startup(depends = "org.jboss.seam.resteasy.bootstrap")
@AutoCreate
public class ResteasyDispatcher extends HttpServletDispatcher
{

    @In
    protected Application application;

    @In
    protected ResteasyBootstrap bootstrap;

    @Logger
    private Log log;

    @Create
    public void init()
    {
        try
        {
            init(null);
        }
        catch (ServletException ex)
        {
            // Can never happen
        }
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException
    {
        // We can skip the init, it only sets up the provider factory (did that already in bootstrap) and
        // the other stuff we do in onStartup(). We also do NOT put the ResteasyProviderFactory, the Dispatcher,
        // nor the Registry into the servlet context. Let's hope RESTEasy code is sane enough not to access the
        // servlet context at runtime...
        log.debug("registering RESTEasy and JAX RS resources and providers");
        setDispatcher(new SynchronousDispatcher(bootstrap.getProviderFactory()));

        getDispatcher().setLanguageMappings(application.getLanguageMappings());
        getDispatcher().setMediaTypeMappings(application.getMediaTypeMappings());

        // Provider registration
        if (application.isUseBuiltinProviders())
        {
            log.info("registering built-in RESTEasy providers");
            RegisterBuiltin.register(getDispatcher().getProviderFactory());
        }

        for (Class providerClass : application.getProviderClasses())
        {
            Set<Component> components = application.getProviderClassComponent(providerClass);
            if (components != null)
            {
                registerSeamComponentProviders(components, providerClass);
            }
            else
            {
                registerProvider(providerClass);
            }
        }

        // Resource registration
        for (final Class resourceClass : application.getClasses())
        {
            // First check if it's a class that is a Seam component
            Set<Component> components = application.getResourceClassComponent(resourceClass);
            if (components != null)
            {
                registerSeamComponentResources(components, resourceClass);
            }
            else
            {
                registerResource(resourceClass);
            }
        }

    }


    protected void registerSeamComponentProviders(Set<Component> components, Class providerClass) {
        for (Component seamComponent : components)
        {
            if (ScopeType.STATELESS.equals(seamComponent.getScope()))
            {
                throw new RuntimeException(
                        "Registration of STATELESS Seam components as RESTEasy providers not implemented!"
                );
            }
            else if (ScopeType.APPLICATION.equals(seamComponent.getScope()))
            {
                Object providerInstance = Component.getInstance(seamComponent.getName());
                boolean isStringConverter = false;
                for (Class componentIface : seamComponent.getBusinessInterfaces())
                {
                    if (StringConverter.class.isAssignableFrom(componentIface))
                    {
                        isStringConverter = true;
                        break;
                    }
                }
                if (isStringConverter)
                {
                    log.error("can't register Seam component as RESTEasy StringConverter, see: https://jira.jboss.org/jira/browse/JBSEAM-4020");
                    //log.debug("registering Seam component as custom RESTEasy string converter provider: " + seamComponent.getName());
                    //getDispatcher().getProviderFactory().addStringConverter((StringConverter)providerInstance);
                }
                else
                {
                    getDispatcher().getProviderFactory().registerProviderInstance(providerInstance);
                }
            }
        }
    }

    protected void registerSeamComponentResources(Set<Component> components, Class resourceClass) {
        for (final Component seamComponent : components)
        {
            // Seam component lookup when call is dispatched to resource
            ResourceFactory factory =
                    new SeamResteasyResourceFactory(resourceClass, seamComponent, getDispatcher().getProviderFactory());

            // Register component on specific path if the component is a ResourceHome or ResourceQuery component configured in components.xml
            if (seamComponent.getBeanClass().equals(ResourceHome.class) || seamComponent.getBeanClass().equals(ResourceQuery.class))
            {

                // We can always instantiate this safely here because it can't have dependencies!
                AbstractResource instance = (AbstractResource) seamComponent.newInstance();
                String path = instance.getPath();
                if (instance.getPath() != null)
                {
                    log.debug("registering resource {0} on path {1} with Seam component lifecycle", seamComponent.getName(), path);
                    getDispatcher().getRegistry().addResourceFactory(factory, path);
                }
                else
                {
                    log.warn("Unable to register {0} resource on null path, check components.xml", seamComponent.getName());
                }
            }
            else
            {
                log.debug("registering resource {0} with Seam component lifecycle", seamComponent.getName());
                getDispatcher().getRegistry().addResourceFactory(factory);
            }
        }

    }

    protected void registerProvider(Class providerClass) {
        // Just plain RESTEasy, no Seam component lookup or lifecycle
        if (StringConverter.class.isAssignableFrom(providerClass))
        {
            log.debug("registering as custom RESTEasy string converter provider class: " + providerClass);
            getDispatcher().getProviderFactory().addStringConverter(providerClass);
        }
        else
        {
            getDispatcher().getProviderFactory().registerProvider(providerClass);
        }
    }

    protected void registerResource(Class resourceClass) {
        // ResourceHome and ResourceQuery have an empty @Path("") and are supposed to be subclassed before use
        // (or through components.xml) - they should be ignored if they are not proper Seam components
        if (ResourceHome.class.equals(resourceClass) || ResourceQuery.class.equals(resourceClass))
            return;

        // Plain EJBs (not a Seam component) can be registered in RESTEasy by JNDI name
        if (resourceClass.isAnnotationPresent(EJB.STATELESS) || resourceClass.isAnnotationPresent(EJB.STATEFUL))
        {
            String jndiName = getJndiName(resourceClass);
            log.debug("registering plain EJB resource as RESTEasy JNDI resource name: " + jndiName);
            getDispatcher().getRegistry().addJndiResource(jndiName);

        }
        else
        {
            // JAX-RS default lifecycle
            log.debug("registering resource {0} with default JAX-RS lifecycle", resourceClass.getName());
            getDispatcher().getRegistry().addResourceFactory(new POJOResourceFactory(resourceClass));
        }
    }

    protected String getJndiName(Class<?> beanClass)
    {
        if (beanClass.isAnnotationPresent(JndiName.class))
        {
            return beanClass.getAnnotation(JndiName.class).value();
        }
        else
        {
            String jndiPattern = Init.instance().getJndiPattern();
            if (jndiPattern == null)
            {
                throw new IllegalArgumentException(
                        "You must specify org.jboss.seam.core.init.jndiPattern or use @JndiName: " + beanClass.getName()
                );
            }
            return jndiPattern.replace("#{ejbName}", Seam.getEjbName(beanClass));
        }
    }

}
