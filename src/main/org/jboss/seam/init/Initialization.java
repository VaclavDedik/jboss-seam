/*
 * JBoss, Home of Professional Open Source
 * 
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.seam.init;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Namespace;
import org.jboss.seam.annotations.Role;
import org.jboss.seam.annotations.Roles;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Jbpm;
import org.jboss.seam.core.ManagedPersistenceContext;
import org.jboss.seam.core.PojoCache;
import org.jboss.seam.debug.Introspector;
import org.jboss.seam.deployment.ComponentScanner;
import org.jboss.seam.deployment.NamespaceScanner;
import org.jboss.seam.persistence.HibernatePersistenceProvider;
import org.jboss.seam.persistence.PersistenceProvider;
import org.jboss.seam.util.Conversions;
import org.jboss.seam.util.DTDEntityResolver;
import org.jboss.seam.util.Naming;
import org.jboss.seam.util.Reflections;
import org.jboss.seam.util.Resources;
import org.jboss.seam.util.Strings;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class Initialization
{
   public static final String COMPONENT_SUFFIX = ".component";
   private static final Log log = LogFactory.getLog(Initialization.class);

   private ServletContext servletContext;
   private Map<String, Conversions.PropertyValue> properties = new HashMap<String, Conversions.PropertyValue>();
   private List<ComponentDescriptor> componentDescriptors = new ArrayList<ComponentDescriptor>();
   private List<FactoryDescriptor> factoryDescriptors = new ArrayList<FactoryDescriptor>();
   private Set<Class> installedComponents = new HashSet<Class>();
   private Set<String> importedPackages = new HashSet<String>();
   private Map<String, NamespaceInfo> namespaceMap = new HashMap<String, NamespaceInfo>();

   public Initialization(ServletContext servletContext)
   {
      this.servletContext = servletContext;

      addNamespaces();
      initComponentsFromXmlDocument();
      initComponentsFromXmlDocuments();
      initPropertiesFromServletContext();
      initPropertiesFromResource();
      initJndiProperties();
   }

   private void initComponentsFromXmlDocuments()
   {
      Enumeration<URL> resources;
      try
      {
         resources = Thread.currentThread().getContextClassLoader().getResources(
                  "META-INF/components.xml");
      }
      catch (IOException ioe)
      {
         throw new RuntimeException("error scanning META-INF/components.xml files", ioe);
      }

      Properties replacements = getReplacements();
      while (resources.hasMoreElements())
      {
         URL url = resources.nextElement();
         try
         {
            log.info("reading " + url);
            installComponentsFromXmlElements(getDocument(url.openStream()), replacements);
         }
         catch (Exception e)
         {
            throw new RuntimeException("error while reading " + url, e);
         }
      }

   }

   private void initComponentsFromXmlDocument()
   {
      InputStream stream = Resources.getResourceAsStream("/WEB-INF/components.xml", servletContext);
      if (stream == null)
      {
         log.info("no /WEB-INF/components.xml file found");
      }
      else
      {
         log.info("reading /WEB-INF/components.xml");
         try
         {
            installComponentsFromXmlElements(getDocument(stream), getReplacements());
         }
         catch (Exception e)
         {
            throw new RuntimeException("error while reading /WEB-INF/components.xml", e);
         }
      }
   }

   private Properties getReplacements()
   {
      try
      {
         Properties replacements = new Properties();
         InputStream replaceStream = Resources.getResourceAsStream("components.properties");
         if (replaceStream != null) replacements.load(replaceStream);
         return replacements;
      }
      catch (IOException ioe)
      {
         throw new RuntimeException("error reading components.properties", ioe);
      }
   }

   private void installComponentsFromXmlElements(Document doc, Properties replacements)
            throws DocumentException, ClassNotFoundException
   {
      List<Element> importElements = doc.getRootElement().elements("import-java-package");
      for (Element importElement : importElements)
      {
         String pkgName = importElement.getTextTrim();
         importedPackages.add(pkgName);
         addNamespace(Package.getPackage(pkgName));
      }

      List<Element> componentElements = doc.getRootElement().elements("component");
      for (Element component : componentElements)
      {
         installComponentFromXmlElement(component, component.attributeValue("name"), component
                  .attributeValue("class"), replacements);
      }

      List<Element> factoryElements = doc.getRootElement().elements("factory");
      for (Element factory : factoryElements)
      {
         installFactoryFromXmlElement(factory);
      }

      for (Element elem : (List<Element>) doc.getRootElement().elements())
      {
         String ns = elem.getNamespace().getURI();
         NamespaceInfo nsInfo = namespaceMap.get(ns);
         if (nsInfo != null)
         {
            String name = elem.attributeValue("name");
            String elemName = toCamelCase( elem.getName() );
            String className = nsInfo.getPackage().getName() + '.' + elemName;
            try
            {
               Class<Object> clazz = Reflections.classForName(className);
               if (name == null)
               {
                  Name nameAnnotation = clazz.getAnnotation(Name.class);
                  if (nameAnnotation!=null) name = nameAnnotation.value();
               }
            }
            catch (ClassNotFoundException cnfe)
            {
               // if it isn't a classname, set
               className = null;
            }

            if (name == null)
            {
               String prefix = nsInfo.getNamespace().prefix();
               name = Strings.isEmpty(prefix) ? 
                     elemName : prefix + '.' + elemName;
            }

            installComponentFromXmlElement(elem, name, className, replacements);
         }
      }
   }

   private void installFactoryFromXmlElement(Element factory)
   {
      String scopeName = factory.attributeValue("scope");
      String name = factory.attributeValue("name");
      if (name == null)
      {
         throw new IllegalArgumentException("must specify name in <factory/> declaration");
      }
      String method = factory.attributeValue("method");
      String value = factory.attributeValue("value");
      if (method == null && value == null)
      {
         throw new IllegalArgumentException(
                  "must specify either method or value in <factory/> declaration for variable: "
                           + name);
      }
      ScopeType scope = scopeName == null ? ScopeType.UNSPECIFIED : ScopeType.valueOf(scopeName
               .toUpperCase());
      boolean autoCreate = "true".equals(factory.attributeValue("auto-create"));
      factoryDescriptors.add(new FactoryDescriptor(name, scope, method, value, autoCreate));
   }

   private Document getDocument(InputStream stream) throws DocumentException
   {
      SAXReader saxReader = new SAXReader();
      saxReader.setEntityResolver(new DTDEntityResolver());
      saxReader.setMergeAdjacentText(true);
      Document doc = saxReader.read(stream);
      return doc;
   }

   private String replace(String value, Properties replacements)
   {
      if (value.startsWith("@"))
      {
         value = replacements.getProperty(value.substring(1, value.length() - 1));
      }
      return value;
   }

   private void installComponentFromXmlElement(Element component, String name, String className,
            Properties replacements) throws ClassNotFoundException
   {
      String installText = component.attributeValue("installed");
      boolean installed = false;
      if (installText == null || "true".equals(replace(installText, replacements)))
      {
         installed = true;
      }

      String scopeName = component.attributeValue("scope");
      String jndiName = component.attributeValue("jndi-name");
      ScopeType scope = scopeName == null ? null : ScopeType.valueOf(scopeName.toUpperCase());
      boolean autoCreate = "true".equals(component.attributeValue("auto-create"));
      if (className != null)
      {
         Class<?> clazz = null;
         try
         {
            clazz = Reflections.classForName(className);
         }
         catch (ClassNotFoundException cnfe)
         {
            for (String pkg : importedPackages)
            {
               try
               {
                  clazz = Reflections.classForName(pkg + '.' + className);
                  break;
               }
               catch (Exception e)
               {
               }
            }
            if (clazz == null) throw cnfe;
         }

         if (name == null)
         {
            name = clazz.getAnnotation(Name.class).value();
         }

         ComponentDescriptor descriptor = new ComponentDescriptor(name, clazz, scope, autoCreate, jndiName, installed);
         componentDescriptors.add(descriptor);
         installedComponents.add(clazz);
      }
      else if (name == null)
      {
         throw new IllegalArgumentException(
                  "must specify either class or name in <component/> declaration");
      }

      for (Element prop : (List<Element>) component.elements())
      {
         String propName = prop.attributeValue("name");
         if (propName == null)
         {
            propName = prop.getQName().getName();
         }
         String qualifiedPropName = name + '.' + toCamelCase(propName);
         properties.put(qualifiedPropName, getPropertyValue(prop, qualifiedPropName, replacements));
      }
   }

   private Conversions.PropertyValue getPropertyValue(Element prop, String propName,
            Properties replacements)
   {
      List<Element> keyElements = prop.elements("key");
      List<Element> valueElements = prop.elements("value");

      Conversions.PropertyValue propertyValue;
      if (valueElements.isEmpty() && keyElements.isEmpty())
      {
         propertyValue = new Conversions.FlatPropertyValue(
                  trimmedText(prop, propName, replacements));
      }
      else if (keyElements.isEmpty())
      {
         // a list-like structure
         int len = valueElements.size();
         String[] values = new String[len];
         for (int i = 0; i < len; i++)
         {
            values[i] = trimmedText(valueElements.get(i), propName, replacements);
         }
         propertyValue = new Conversions.MultiPropertyValue(values);
      }
      else
      {
         // a map-like structure
         if (valueElements.size() != keyElements.size())
         {
            throw new IllegalArgumentException("value elements must match key elements: "
                     + propName);
         }
         Map<String, String> keyedValues = new HashMap<String, String>();
         for (int i = 0; i < keyElements.size(); i++)
         {
            String key = trimmedText(keyElements.get(i), propName, replacements);
            String value = trimmedText(valueElements.get(i), propName, replacements);
            keyedValues.put(key, value);
         }
         propertyValue = new Conversions.AssociativePropertyValue(keyedValues);
      }
      return propertyValue;

   }

   private String trimmedText(Element element, String propName, Properties replacements)
   {
      String text = element.getTextTrim();
      if (text == null)
      {
         throw new IllegalArgumentException("property value must be specified in element body: "
                  + propName);
      }
      return replace(text, replacements);
   }

   public Initialization setProperty(String name, Conversions.PropertyValue value)
   {
      properties.put(name, value);
      return this;
   }

   public Initialization init()
   {
      log.info("initializing Seam");
      scanForComponents();
      Lifecycle.beginInitialization(servletContext);
      Contexts.getApplicationContext().set(Component.PROPERTIES, properties);
      addComponents();
      Lifecycle.endInitialization();
      log.info("done initializing Seam");
      return this;
   }

   private void scanForComponents()
   {
      Set<Package> scannedPackages = new HashSet<Package>();
      for (Class<Object> scannedClass : new ComponentScanner("seam.properties").getClasses())
      {
         installScannedClass(scannedPackages, scannedClass);
      }
      for (Class<Object> scannedClass : new ComponentScanner("META-INF/components.xml")
               .getClasses())
      {
         installScannedClass(scannedPackages, scannedClass);
      }
   }

   private void installScannedClass(Set<Package> scannedPackages, Class<Object> scannedClass)
   {
      installScannedComponentAndRoles(scannedClass);
      installComponentsFromDescriptor(classDescriptorFilename(scannedClass), scannedClass);
      Package pkg = scannedClass.getPackage();
      if (pkg != null && scannedPackages.add(pkg))
      {
         installComponentsFromDescriptor(packageDescriptorFilename(pkg), scannedClass);
      }
   }

   private static String classDescriptorFilename(Class<Object> scannedClass)
   {
      return scannedClass.getName().replace('.', '/') + ".component.xml";
   }

   private static String packageDescriptorFilename(Package pkg)
   {
      return pkg.getName().replace('.', '/') + "/components.xml";
   }

   private void installComponentsFromDescriptor(String fileName, Class clazz)
   {
      //note: this is correct, we do not need to scan other classloaders!
      InputStream stream = clazz.getClassLoader().getResourceAsStream(fileName); 
      if (stream != null)
      {
         try
         {
            Properties replacements = getReplacements();
            Document doc = getDocument(stream);
            if (doc.getRootElement().getName().equals("components"))
            {
               installComponentsFromXmlElements(doc, replacements);
            }
            else
            {
               //TODO: namespaced components!!!
               installComponentFromXmlElement(doc.getRootElement(), doc.getRootElement()
                        .attributeValue("name"), clazz.getName(), replacements);
            }
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   private void installScannedComponentAndRoles(Class<Object> scannedClass)
   {
      if (scannedClass.isAnnotationPresent(Name.class))
      {
         componentDescriptors.add(new ComponentDescriptor(scannedClass));
      }
      if (scannedClass.isAnnotationPresent(Role.class))
      {
         installRole(scannedClass, scannedClass.getAnnotation(Role.class));
      }
      if (scannedClass.isAnnotationPresent(Roles.class))
      {
         Role[] roles = scannedClass.getAnnotation(Roles.class).value();
         for (Role role : roles)
         {
            installRole(scannedClass, role);
         }
      }
   }

   private void installRole(Class<Object> scannedClass, Role role)
   {
      ScopeType scope = Seam.getComponentRoleScope(scannedClass, role);
      componentDescriptors.add(new ComponentDescriptor(role.name(), scannedClass, scope, false,
               null, null));
   }

   private void addNamespace(Package pkg)
   {
      if (pkg != null)
      {
         Namespace ns = pkg.getAnnotation(Namespace.class);
         if (ns != null)
         {
            log.info("Mapping namespace " + ns.value() + "  to package " + pkg.getName()
                     + " with prefix=" + ns.prefix());
            namespaceMap.put(ns.value(), new NamespaceInfo(ns, pkg));
         }
      }
   }

   private void addNamespaces()
   {
      addNamespace(Init.class.getPackage());
      // need to solve the problem of forcing a package to load
      addNamespace(org.jboss.seam.framework.Home.class.getPackage());
      addNamespace(org.jboss.seam.jms.TopicSession.class.getPackage());
      addNamespace(org.jboss.seam.remoting.RequestContext.class.getPackage());
      addNamespace(org.jboss.seam.theme.Theme.class.getPackage());

      for (Package pkg : new NamespaceScanner("seam.properties").getPackages())
      {
         addNamespace(pkg);
      }
      for (Package pkg : new NamespaceScanner("META-INF/components.xml").getPackages())
      {
         addNamespace(pkg);
      }
   }

   private void initPropertiesFromServletContext()
   {
      Enumeration params = servletContext.getInitParameterNames();
      while (params.hasMoreElements())
      {
         String name = (String) params.nextElement();
         properties.put(name, new Conversions.FlatPropertyValue(servletContext
                  .getInitParameter(name)));
      }
   }

   private void initPropertiesFromResource()
   {
      Properties props = loadFromResource("/seam.properties");
      for (Map.Entry me : props.entrySet())
      {
         properties.put((String) me.getKey(), new Conversions.FlatPropertyValue((String) me
                  .getValue()));
      }
   }

   private void initJndiProperties()
   {
      Properties jndiProperties = new Properties();
      jndiProperties.putAll(loadFromResource("/jndi.properties"));
      jndiProperties.putAll(loadFromResource("/seam-jndi.properties"));
      Naming.setInitialContextProperties(jndiProperties);
   }

   private Properties loadFromResource(String resource)
   {
      Properties props = new Properties();
      InputStream stream = Resources.getResourceAsStream(resource, servletContext);
      if (stream != null)
      {
         log.info("reading properties from: " + resource);
         try
         {
            props.load(stream);
         }
         catch (IOException ioe)
         {
            log.error("could not read " + resource, ioe);
         }
      }
      else
      {
         log.debug("not found: " + resource);
      }
      return props;
   }

   protected ComponentDescriptor findDescriptor(Class<?> componentClass)
   {
      for (ComponentDescriptor component : componentDescriptors)
      {
         if (component.getComponentClass().equals(componentClass))
         {
            return component;
         }
      }
      return null;
   }

   protected void addComponents()
   {
      Context context = Contexts.getApplicationContext();

      // force instantiation of Init
      addComponent(new ComponentDescriptor(Init.class), context);
      Init init = (Init) Component.getInstance(Init.class, ScopeType.APPLICATION);

      ComponentDescriptor desc = findDescriptor(Jbpm.class);
      if (desc != null && desc.isInstalled())
      {
         init.setJbpmInstalled(true);
      }

      try
      {
         Reflections.classForName("org.jboss.cache.aop.PojoCache");
         componentDescriptors.add( new ComponentDescriptor(PojoCache.class, true) );
      }
      catch (ClassNotFoundException e) {}

      if (installedComponents.contains(ManagedPersistenceContext.class))
      {
         try
         {
            Reflections.classForName("org.hibernate.Session");
            componentDescriptors.add( new ComponentDescriptor(HibernatePersistenceProvider.class, true) );
         }
         catch (ClassNotFoundException cnfe)
         {
            componentDescriptors.add( new ComponentDescriptor(PersistenceProvider.class, true) );
         }
      }

      if (init.isDebug())
      {
         componentDescriptors.add( new ComponentDescriptor(Introspector.class, true) );
         componentDescriptors.add( new ComponentDescriptor(org.jboss.seam.debug.Contexts.class, true) );
      }

      log.info("Installing components...");
      boolean installedSomething = false;
      do
      {
         installedSomething = false;
         for (ComponentDescriptor componentDescriptor : componentDescriptors)
         {
            String compName = componentDescriptor.getName() + COMPONENT_SUFFIX;
            if ( !context.isSet(compName) && dependenciesMet(context, componentDescriptor) )
            {
               addComponent(componentDescriptor, context);
               if (componentDescriptor.isAutoCreate())
               {
                  init.addAutocreateVariable(componentDescriptor.getName());
               }
               installedSomething = true;
            }
         }

      }
      while (installedSomething);

      for (FactoryDescriptor factoryDescriptor : factoryDescriptors)
      {
         if (factoryDescriptor.isValueBinding())
         {
            init.addFactoryValueBinding(factoryDescriptor.getName(), factoryDescriptor.getValue(),
                     factoryDescriptor.getScope());
         }
         else
         {
            init.addFactoryMethodBinding(factoryDescriptor.getName(),
                     factoryDescriptor.getMethod(), factoryDescriptor.getScope());
         }
         if (factoryDescriptor.isAutoCreate())
         {
            init.addAutocreateVariable(factoryDescriptor.getName());
         }
      }
   }

   protected boolean dependenciesMet(Context context, ComponentDescriptor descriptor)
   {
      if ( !descriptor.isInstalled() ) return false;
      
      String[] dependencies = descriptor.getDependencies();
      if (dependencies!=null)
      {
         for (String dependency: dependencies)
         {
            if ( !context.isSet(dependency + COMPONENT_SUFFIX) )
            {
               return false;
            }
         }
      }
      Class[] genericDependencies = descriptor.getGenericDependencies();
      if (genericDependencies!=null)
      {
         for (Class genericDependency: genericDependencies)
         {
            if ( !installedComponents.contains(genericDependency) )
            {
               return false;
            }
         }
      }
      return true;
   }

   /**
    * This actually creates a propert component and should only be called when
    * we want to install a component
    */
   protected void addComponent(ComponentDescriptor descriptor, Context context)
   {
      String name = descriptor.getName();
      String componentName = name + COMPONENT_SUFFIX;

      if (log.isWarnEnabled() && context.isSet(componentName))
      {
         log.warn("Component has been previously installed and is being redefined: " + name);
      }

      Component component = new Component(descriptor.getComponentClass(), name, descriptor
               .getScope(), descriptor.getJndiName());
      context.set(componentName, component);
   }

   private static String toCamelCase(String hyphenated)
   {
      StringTokenizer tokens = new StringTokenizer(hyphenated, "-");
      StringBuilder result = new StringBuilder( hyphenated.length() )
            .append( tokens.nextToken() );
      while ( tokens.hasMoreTokens() )
      {
         String token = tokens.nextToken();
         result.append( Character.toUpperCase( token.charAt(0) ) )
               .append( token.substring(1) );
      }
      return result.toString();
   }

   private static class FactoryDescriptor
   {
      private String name;
      private ScopeType scope;
      private String method;
      private String value;
      private boolean autoCreate;

      public FactoryDescriptor(String name, ScopeType scope, String method, String value,
               boolean autoCreate)
      {
         super();
         this.name = name;
         this.scope = scope;
         this.method = method;
         this.value = value;
         this.autoCreate = autoCreate;
      }

      public String getMethod()
      {
         return method;
      }

      public String getValue()
      {
         return value;
      }

      public String getName()
      {
         return name;
      }

      public ScopeType getScope()
      {
         return scope;
      }

      public boolean isValueBinding()
      {
         return method == null;
      }

      public boolean isAutoCreate()
      {
         return autoCreate;
      }

      @Override
      public String toString()
      {
         return "FactoryDescriptor(" + name + ')';
      }
   }

   private static class NamespaceInfo
   {
      private Namespace namespace;
      private Package pkg;

      public NamespaceInfo(Namespace namespace, Package pkg)
      {
         this.namespace = namespace;
         this.pkg = pkg;
      }

      public Namespace getNamespace()
      {
         return namespace;
      }

      public Package getPackage()
      {
         return pkg;
      }

   }

   private static class ComponentDescriptor
   {
      private String name;
      private Class<?> componentClass;
      private ScopeType scope;
      private String jndiName;
      private Boolean installed;
      private boolean autoCreate;

      public ComponentDescriptor(String name, Class<?> componentClass, ScopeType scope,
               boolean autoCreate, String jndiName, Boolean installed)
      {
         this.name = name;
         this.componentClass = componentClass;
         this.scope = scope;
         this.jndiName = jndiName;
         this.installed = installed;
         this.autoCreate = autoCreate;
      }

      public ComponentDescriptor(Class componentClass)
      {
         this.componentClass = componentClass;
      }

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
         return autoCreate;
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

      public boolean isInstalled()
      {
         if (installed != null)
         {
            return installed;
         }
         Install anno = componentClass.getAnnotation(Install.class);
         if (anno == null)
         {
            return true;
         }
         return anno.value();
      }

      @Override
      public String toString()
      {
         return "ComponentDescriptor(" + getName() + ')';
      }
   }

}
