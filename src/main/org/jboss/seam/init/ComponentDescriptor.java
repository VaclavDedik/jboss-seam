package org.jboss.seam.init;

import java.util.Comparator;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.core.Init;
import org.jboss.seam.servlet.AbstractResource;

public class ComponentDescriptor 
    implements Comparable<ComponentDescriptor>
{
    protected String name;
    protected Class<?> componentClass;
    protected ScopeType scope;
    protected String jndiName;
    protected Boolean installed;
    protected boolean autoCreate;
    protected Integer precedence;

    /**
     * For components.xml
     */
    public ComponentDescriptor(String name, Class<?> componentClass, ScopeType scope,
            boolean autoCreate, String jndiName, Boolean installed, Integer precedence) 
    {
        this.name = name;
        this.componentClass = componentClass;
        this.scope = scope;
        this.jndiName = jndiName;
        this.installed = installed;
        this.autoCreate = autoCreate;
        this.precedence = precedence;
    }

    /**
     * For a scanned role
     */
    public ComponentDescriptor(String name, Class<?> componentClass, ScopeType scope)
    {
        this.name = name;
        this.componentClass = componentClass;
        this.scope = scope;
    }

    /**
     * For a scanned default role
     */
    public ComponentDescriptor(Class componentClass)
    {
        this.componentClass = componentClass;
    }

    /**
     * For built-ins with special rules
     */
    public ComponentDescriptor(Class componentClass, Boolean installed)
    {
        this.componentClass = componentClass;
        this.installed = installed;

    }

    public String getName()
    {
        return name == null ? Seam.getComponentName(componentClass) : name;
    }

    public ScopeType getScope()
    {
        return scope == null ? Seam.getComponentScope(componentClass) : scope;
    }

    public Class getComponentClass()
    {
        return componentClass;
    }

    public String getJndiName()
    {
        return jndiName;
    }

    public boolean isAutoCreate()
    {
        return autoCreate || componentClass.isAnnotationPresent(AutoCreate.class);
    }

    public String[] getDependencies()
    {
        Install install = componentClass.getAnnotation(Install.class);
        if (install == null)
        {
            return null;
        }
        return install.dependencies();
    }

    public Class[] getGenericDependencies()
    {
        
        Install install = componentClass.getAnnotation(Install.class);
        if (install == null)
        {
            return null;
        }
        return install.genericDependencies();
    }

    public String[] getClassDependencies() 
    {
        Install install = componentClass.getAnnotation(Install.class);
        if (install == null)
        {
            return null;
        }
        return install.classDependencies();  
    }

    public boolean isInstalled()
    {
        if (installed != null)
        {
            return installed;
        }
        Install install = componentClass.getAnnotation(Install.class);
        if (install == null)
        {
            return true;
        }
        return install.debug() ? Init.instance().isDebug() : install.value();
    }

    public int getPrecedence()
    {
        if (precedence != null)
        {
            return precedence;
        }
        Install install = componentClass.getAnnotation(Install.class);
        if (install == null)
        {
            return Install.APPLICATION;
        }
        return install.precedence();
    }

    public int compareTo(ComponentDescriptor other)
    {
        return other.getPrecedence() - getPrecedence();
    }

    public boolean isFilter()
    {
        if (javax.servlet.Filter.class.isAssignableFrom(componentClass))
        {
           for (Class clazz = componentClass; !Object.class.equals(clazz); clazz = clazz.getSuperclass())
           {
              if (clazz.isAnnotationPresent(org.jboss.seam.annotations.Filter.class))
              {
                 return true;
              }
           }
        }
        return false;
    }

    public boolean isResourceProvider()
    {
        return AbstractResource.class.isAssignableFrom(componentClass);
    }   
    
    @Override
    public String toString()
    {
        return "ComponentDescriptor(" + getName() + ":" + getComponentClass() + ')';
    }
            
    public static class PrecedenceComparator    
         implements Comparator<ComponentDescriptor>
   {               
        public int compare(ComponentDescriptor obj1, ComponentDescriptor obj2) {            
            return obj2.getPrecedence() - obj1.getPrecedence();
        }
    }
}
