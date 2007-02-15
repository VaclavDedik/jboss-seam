package org.jboss.seam.ioc.spring;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * Provides an instance of a Seam Component in the current context given the name.
 *
 * @author youngm
 */
public class SeamFactoryBean 
    extends AbstractFactoryBean 
    implements InitializingBean
{
    private ScopeType scope;
    private String name;
    private Boolean create;

    public SeamFactoryBean()
    {
        setSingleton(false);
    }
    
    
    /**
     * Ensure name is not null
     *
     * @see org.springframework.beans.factory.config.AbstractFactoryBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() 
        throws Exception
    {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        super.afterPropertiesSet();
    }

    /**
     * Return the current instance of a Seam component given the current context.
     *
     * @see org.springframework.beans.factory.config.AbstractFactoryBean#createInstance()
     */
    @Override
    protected Object createInstance() 
        throws Exception
    {
        if (scope == null && create == null) {
            return Component.getInstance(name);
        } else if (scope == null) {
            return Component.getInstance(name, create);
        } else if (create == null) {
            return Component.getInstance(name, scope);
        } else {
            return Component.getInstance(name, scope, create);
        }
    }
    
    /**
     * Return the type of the component if it is available.
     *
     * @see org.springframework.beans.factory.config.AbstractFactoryBean#getObjectType()
     */
    @Override
    public Class getObjectType()
    {
        Component component = Component.forName(name);
        if (component == null) {
            return null;
        }
        return component.getBeanClass();
    }
    
    /**
     * The seam component name
     *
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * The seam component scope (optional)
     *
     * @param scope the scope to set
     */
    public void setScope(ScopeType scope)
    {
        this.scope = scope;
    }
    
    /**
     * Weather to create an instance of the component if one doesn't already exist in this context.
     *
     * @param create the create to set
     */
    public void setCreate(Boolean create) {
        this.create = create;
    }
}
