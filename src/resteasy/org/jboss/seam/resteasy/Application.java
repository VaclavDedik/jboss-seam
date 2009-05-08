package org.jboss.seam.resteasy;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Install;

import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * Resteasy and JAX-RS configuration, override in components.xml to
 * customize Resteasy settings.
 *
 * @author Christian Bauer
 */
@Name("org.jboss.seam.resteasy.application")
@Scope(ScopeType.APPLICATION)
@Install(precedence = Install.BUILT_IN)
@AutoCreate
public class Application extends javax.ws.rs.core.Application
{

   final private Map<Class<?>, Set<Component>> providerClasses = new HashMap<Class<?>, Set<Component>>();
   final private Map<Class<?>, Set<Component>> resourceClasses = new HashMap<Class<?>, Set<Component>>();
   
    private List<String> providerClassNames = new ArrayList<String>();
    private List<String> resourceClassNames = new ArrayList<String>();

    private Map<String, String> mediaTypeMappings = new HashMap<String, String>();
    private Map<String, String> languageMappings = new HashMap<String, String>();

    private boolean scanProviders = true;
    private boolean scanResources = true;
    private boolean useBuiltinProviders = true;
    private boolean destroySessionAfterRequest = true;

    private String resourcePathPrefix = "/rest";
    private boolean stripSeamResourcePath = true;

    public Application()
    {
        super();
    }

    public Set<Class<?>> getProviderClasses()
    {
        return providerClasses.keySet();
    }

    @Override
    public Set<Class<?>> getClasses()
    {
        return resourceClasses.keySet();
    }

   public void addProviderClass(Class<?> clazz)
   {
      providerClasses.put(clazz, null);
   }

   public void addProviderClass(Class<?> clazz, Component component)
   {
      Set<Component> components = providerClasses.get(clazz);
      if (components == null)
      {
         components = new HashSet<Component>();
         providerClasses.put(clazz, components);
      }
      components.add(component);
   }

    public void removeProviderClass(Class<?> clazz)
    {
        providerClasses.remove(clazz);
    }

   public void addResourceClass(Class<?> clazz)
   {
      resourceClasses.put(clazz, null);
   }

   public void addResourceClass(Class<?> clazz, Set<Component> newComponents)
   {
      Set<Component> components = resourceClasses.get(clazz);
      if (components == null)
      {
         components = new HashSet<Component>();
         resourceClasses.put(clazz, components);
      }
      components.addAll(newComponents);
   }

    public void removeResourceClass(Class<?> clazz)
    {
        resourceClasses.remove(clazz);
    }

   public Set<Component> getProviderClassComponent(Class clazz)
   {
      return providerClasses.get(clazz) != null ? providerClasses.get(clazz) : null;
   }

   public Set<Component> getResourceClassComponent(Class clazz)
   {
      return resourceClasses.get(clazz) != null ? resourceClasses.get(clazz) : null;
   }

    public Map<String, MediaType> getMediaTypeMappings()
    {
        Map<String, MediaType> extMap = new HashMap<String, MediaType>();
        for (String ext : mediaTypeMappings.keySet())
        {
            String value = mediaTypeMappings.get(ext);
            extMap.put(ext, MediaType.valueOf(value));
        }
        return extMap;
    }

    public void setMediaTypeMappings(Map<String, String> mediaTypeMappings)
    {
        this.mediaTypeMappings = mediaTypeMappings;
    }

    public Map<String, String> getLanguageMappings()
    {
        return languageMappings;
    }

    public void setLanguageMappings(Map<String, String> languageMappings)
    {
        this.languageMappings = languageMappings;
    }

    public List<String> getProviderClassNames()
    {
        return providerClassNames;
    }

    public void setProviderClassNames(List<String> providerClassNames)
    {
        this.providerClassNames = providerClassNames;
    }

    public List<String> getResourceClassNames()
    {
        return resourceClassNames;
    }

    public void setResourceClassNames(List<String> resourceClassNames)
    {
        this.resourceClassNames = resourceClassNames;
    }

    public boolean isScanProviders()
    {
        return scanProviders;
    }

    public void setScanProviders(boolean scanProviders)
    {
        this.scanProviders = scanProviders;
    }

    public boolean isScanResources()
    {
        return scanResources;
    }

    public void setScanResources(boolean scanResources)
    {
        this.scanResources = scanResources;
    }

    public boolean isUseBuiltinProviders()
    {
        return useBuiltinProviders;
    }

    public void setUseBuiltinProviders(boolean useBuiltinProviders)
    {
        this.useBuiltinProviders = useBuiltinProviders;
    }

    public boolean isDestroySessionAfterRequest()
    {
        return destroySessionAfterRequest;
    }

    public void setDestroySessionAfterRequest(boolean destroySessionAfterRequest)
    {
        this.destroySessionAfterRequest = destroySessionAfterRequest;
    }

    public String getResourcePathPrefix()
    {
        return resourcePathPrefix;
    }

    public void setResourcePathPrefix(String resourcePathPrefix)
    {
        this.resourcePathPrefix = resourcePathPrefix;
    }

    public boolean isStripSeamResourcePath()
    {
        return stripSeamResourcePath;
    }

    public void setStripSeamResourcePath(boolean stripSeamResourcePath)
    {
        this.stripSeamResourcePath = stripSeamResourcePath;
    }
}
