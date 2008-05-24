package org.jboss.seam.init;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.jboss.seam.ComponentType;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Resources;
import org.jboss.seam.util.XML;

/**
 * Parser for ejb-jar.xml and orm.xml deployment descriptors
 * 
 * @author Norman Richards
 *
 */
public class DeploymentDescriptor 
{ 
    private static final LogProvider log = Logging.getLogProvider(Initialization.class);
    
    private Map<Class, EjbDescriptor> ejbDescriptors = new HashMap<Class, EjbDescriptor>();
    private Class componentClass;

    public DeploymentDescriptor(Class clazz) 
    {
       componentClass = clazz;
       if (clazz.getClassLoader() == null) {
           return;
       }       
       
        try 
        {
            InputStream ejbJarXml = Resources.getResourceAsStream("META-INF/ejb-jar.xml", null);
            if (ejbJarXml!=null)
            {
               parseEjbJarXml( XML.getRootElementSafely(ejbJarXml) );
            }
        } 
        catch (DocumentException e) 
        {
            log.warn("Couldn't parse META-INF/ejb-jar.xml for component types " + e.getMessage());
        }

        try 
        {
            InputStream ormXml = Resources.getResourceAsStream("META-INF/orm.xml", null);
            if (ormXml!=null)
            {
               parseOrmXml( XML.getRootElementSafely(ormXml) );
            }
        } 
        catch (DocumentException e) 
        {
            log.warn("Couldn't parse META-INF/orm.xml for component types " + e.getMessage());
        }
    }
    
    public Map<Class, EjbDescriptor> getEjbDescriptors()
    {
       return ejbDescriptors;
    }
    
    
    @SuppressWarnings("unchecked")
    private void parseEjbJarXml(Element root) 
    {

        Element beans = root.element("enterprise-beans");
        if (beans != null) 
        {
            for (Element bean: (List<Element>) beans.elements("session")) 
            {
                EjbDescriptor info = new EjbDescriptor();
                info.setEjbName(bean.element("ejb-name").getTextTrim());
                Element ejbClass = bean.element("ejb-class");
                if (ejbClass != null)
                {
                   info.setEjbClassName(ejbClass.getTextTrim());
                   Element sessionType = bean.element("session-type");
                   if (sessionType != null && sessionType.getTextTrim().equalsIgnoreCase("Stateful")) 
                   {
                       info.setBeanType(ComponentType.STATEFUL_SESSION_BEAN);            
                   } 
                   else 
                   {
                       info.setBeanType(ComponentType.STATELESS_SESSION_BEAN);     
                   }
                   add(info);
                }
            }          
            for (Element bean: (List<Element>) beans.elements("message-driven")) 
            {
                EjbDescriptor info = new EjbDescriptor();
                info.setEjbName(bean.element("ejb-name").getTextTrim());
                info.setEjbClassName(bean.element("ejb-class").getTextTrim());
                info.setBeanType(ComponentType.MESSAGE_DRIVEN_BEAN);
                add(info);
            }      
        }
    }
        
    @SuppressWarnings("unchecked")
    private void parseOrmXml(Element root) 
    {
        String packagePrefix = "";
        
        Element pkg = root.element("package");
        if (pkg!=null) 
        {
            packagePrefix = pkg.getTextTrim() + ".";
        }
        
        String defaultAccessType = getDefaultAccessType(root);
        
        boolean defaultMetadataComplete = isDefaultMetadataComplete(root);
                
        for (Element entity: (List<Element>) root.elements("entity")) 
        {
            String className = packagePrefix + entity.attribute("class").getText();
            EjbEntityDescriptor info = new EjbEntityDescriptor();
            info.setBeanType(ComponentType.ENTITY_BEAN);
            info.setEjbClassName(className);
            info.setEjbName(entity.attribute("name") != null ? entity.attribute("name").getText() : null);
            if (defaultMetadataComplete || entity.attribute("metadata-complete") != null)
            {
               info.setMetaDataComplete(defaultMetadataComplete || "true".equals(entity.attribute("metadata-complete").getText()));
            }
            info.setPreRemoveMethodName(getEntityCallback(entity, "pre-remove"));
            info.setPrePersistMethodName(getEntityCallback(entity, "pre-persist"));
            info.setPreUpdateMethodName(getEntityCallback(entity, "pre-update"));
            info.setPostLoadMethodName(getEntityCallback(entity, "post-load"));
            info.setIdentifierAttribute(getEntityAttributeName(entity, "id"), getAccessType(getEntityAttributeAccessType(entity, "id"), defaultAccessType));
            info.setVersionAttribute(getEntityAttributeName(entity, "version"), getAccessType(getEntityAttributeAccessType(entity, "version"), defaultAccessType));
            add(info);
        }
    }
    
    private static String getEntityCallback(Element parent, String callbackName)
    {
       Element callbackElement = parent.element(callbackName);
       if (callbackElement != null)
       {
          return callbackElement.attribute("method-name").getText();
       }
       return null;
    }
    
    private static String getEntityAttributeName(Element entity, String attributeName)
    {
       if (entity.element("attributes") != null && entity.element("attributes").element(attributeName) != null)
       {
          return entity.element("attributes").element(attributeName).attribute("name").getText(); 
       }
       
       return null;
       
    }
    
    private static String getEntityAttributeAccessType(Element entity, String attributeName)
    {
       if (entity.element("attributes") != null && entity.element("attributes").element(attributeName) != null)
       {
          if (entity.element("attributes").element(attributeName).attribute("access") != null)
          {
             return entity.element("attributes").element(attributeName).attribute("access").getText();
          }
       }
       
       return null;
    }
    
    private static String getDefaultAccessType(Element root)
    {
       if (root.element("access") != null)
       {
          return root.element("access").getText();
       }
       else
       {
          if (root.element("persistence-unit-metadata") != null && root.element("persistence-unit-metadata").element("persistence-unit-defaults") != null && root.element("persistence-unit-metadata").element("persistence-unit-defaults").element("access") != null )
          {
             return root.element("persistence-unit-metadata").element("persistence-unit-defaults").element("access").getText();
          }
       }
       return null;
    }
    
    private static boolean isDefaultMetadataComplete(Element root)
    {
       return root.element("persistence-unit-metadata") != null && root.element("persistence-unit-metadata").element("xml-mapping-metadata-complete") != null;
    }
    
    
    private static String getAccessType(String accessType, String defaultAccessType)
    {
       return accessType != null ? accessType : (defaultAccessType != null ? defaultAccessType : "FIELD");
    }

    protected void add(EjbDescriptor descriptor) 
    {
       try
       {
          Class ejbClass = componentClass.getClassLoader().loadClass( descriptor.getEjbClassName() );
          ejbDescriptors.put(ejbClass, descriptor);
       }
       catch (ClassNotFoundException cnfe)
       {
          log.warn("Could not load EJB class: " + descriptor.getEjbClassName());
       }
    }
}
