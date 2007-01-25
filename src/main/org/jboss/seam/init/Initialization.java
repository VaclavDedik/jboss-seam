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

import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;
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
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.persistence.HibernatePersistenceProvider;
import org.jboss.seam.persistence.PersistenceProvider;
import org.jboss.seam.util.Conversions;
import org.jboss.seam.util.Naming;
import org.jboss.seam.util.Reflections;
import org.jboss.seam.util.Resources;
import org.jboss.seam.util.Strings;
import org.jboss.seam.util.XML;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class Initialization
{
   public static final String COMPONENT_SUFFIX = ".component";
   private static final LogProvider log = Logging.getLogProvider(Initialization.class);

   private ServletContext servletContext;
   private Map<String, Conversions.PropertyValue> properties = new HashMap<String, Conversions.PropertyValue>();
   private Map<String, ComponentDescriptor> componentDescriptors = new HashMap<String, ComponentDescriptor>();
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
            installComponentsFromXmlElements( XML.getRootElement( url.openStream() ), replacements );
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
            installComponentsFromXmlElements( XML.getRootElement(stream), getReplacements() );
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

   private void installComponentsFromXmlElements(Element rootElement, Properties replacements)
            throws DocumentException, ClassNotFoundException
   {
      List<Element> importElements = rootElement.elements("import-java-package");
      for (Element importElement : importElements)
      {
         String pkgName = importElement.getTextTrim();
         importedPackages.add(pkgName);
         addNamespace(Package.getPackage(pkgName));
      }

      List<Element> componentElements = rootElement.elements("component");
      for (Element component : componentElements)
      {
         installComponentFromXmlElement(component, component.attributeValue("name"), component
                  .attributeValue("class"), replacements);
      }

      List<Element> factoryElements = rootElement.elements("factory");
      for (Element factory : factoryElements)
      {
         installFactoryFromXmlElement(factory);
      }

      for (Element elem : (List<Element>) rootElement.elements())
      {
         String ns = elem.getNamespace().getURI();
         NamespaceInfo nsInfo = namespaceMap.get(ns);
         if (nsInfo != null)
         {
            String name = elem.attributeValue("name");
            String elemName = toCamelCase( elem.getName(), true );
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
      String precedenceString = component.attributeValue("precedence");
      int precedence = precedenceString==null ? Install.APPLICATION : Integer.valueOf(precedenceString);
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
            if ( !clazz.isAnnotationPresent(Name.class) )
            {
               throw new IllegalArgumentException(
                        "Component class must have @Name annotation or name must be specified in components.xml: " +
                        clazz.getName());
            }
            
            name = clazz.getAnnotation(Name.class).value();
         }

         ComponentDescriptor descriptor = new ComponentDescriptor(name, clazz, scope, autoCreate, jndiName, installed, precedence);
         addComponentDescriptor(descriptor);
         installedComponents.add(clazz);
      }
      else if (name == null)
      {
         throw new IllegalArgumentException("must specify either class or name in <component/> declaration");
      }

      for (Element prop : (List<Element>) component.elements())
      {
         String propName = prop.attributeValue("name");
         if (propName == null)
         {
            propName = prop.getQName().getName();
         }
         String qualifiedPropName = name + '.' + toCamelCase(propName, false);
         properties.put(qualifiedPropName, getPropertyValue(prop, qualifiedPropName, replacements));
      }
      
      for (Attribute prop: (List<Attribute>) component.attributes())
      {
         String attributeName = prop.getName();
         boolean isProperty = !"name".equals(attributeName) && 
               !"installed".equals(attributeName) && 
               !"scope".equals(attributeName) &&
               !"class".equals(attributeName) &&
               !"jndi-name".equals(attributeName) &&
               !"precedence".equals(attributeName) &&
               !"auto-create".equals(attributeName);
         if (isProperty)
         {
            String qualifiedPropName = name + '.' + toCamelCase( prop.getQName().getName(), false );
            properties.put(qualifiedPropName, getPropertyValue(prop, replacements));
         }
      }
   }

   private void addComponentDescriptor(ComponentDescriptor descriptor)
   {
      String name = descriptor.getName();
      ComponentDescriptor existing = componentDescriptors.get( name );
      boolean newHasPrecedence = existing!=null && existing.getPrecedence()<descriptor.getPrecedence();
      boolean oldHasPrecedence = existing!=null && existing.getPrecedence()>descriptor.getPrecedence();
      if ( newHasPrecedence || oldHasPrecedence )
      {
         log.info("two components with same name, higher precedence wins: " + name);
      }
      if ( existing==null || newHasPrecedence )
      {
         componentDescriptors.put(name, descriptor);
      }
      else if ( existing.getPrecedence()==descriptor.getPrecedence() )
      {
         throw new IllegalStateException("Two components with the same name and precedence: " + name);
      }
   }

   private Conversions.PropertyValue getPropertyValue(Attribute prop, Properties replacements)
   {
      return new Conversions.FlatPropertyValue( trimmedText(prop, replacements) );
   }
   
   private Conversions.PropertyValue getPropertyValue(Element prop, String propName,
            Properties replacements)
   {
      List<Element> keyElements = prop.elements("key");
      List<Element> valueElements = prop.elements("value");

      if (valueElements.isEmpty() && keyElements.isEmpty())
      {
         return new Conversions.FlatPropertyValue(
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
         return new Conversions.MultiPropertyValue(values);
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
         return new Conversions.AssociativePropertyValue(keyedValues);
      }
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

   private String trimmedText(Attribute attribute, Properties replacements)
   {
      return replace( attribute.getText(), replacements );
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
      Set<Class<Object>> scannedClasses = new HashSet<Class<Object>>();
      scannedClasses.addAll( new ComponentScanner("seam.properties").getClasses() );
      scannedClasses.addAll( new ComponentScanner("META-INF/seam.properties").getClasses() );
      scannedClasses.addAll( new ComponentScanner("META-INF/components.xml").getClasses() );
      for (Class<Object> scannedClass: scannedClasses)
      {
         installScannedClass(scannedPackages, scannedClass);
      }
   }

   private void installScannedClass(Set<Package> scannedPackages, Class<Object> scannedClass)
   {
      installScannedComponentAndRoles(scannedClass);
      installComponentsFromDescriptor(classDescriptorFilename(scannedClass), scannedClass);
      Package pkg = scannedClass.getPackage();
      if (pkg != null && scannedPackages.add(pkg) )
      {
         installComponentsFromDescriptor( packageDescriptorFilename(pkg), scannedClass );
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
            Element root = XML.getRootElement(stream);
            if ( root.getName().equals("components") )
            {
               installComponentsFromXmlElements(root, replacements);
            }
            else
            {
               //TODO: namespaced components!!!
               installComponentFromXmlElement(
                        root, 
                        root.attributeValue("name"), 
                        clazz.getName(), replacements
                     );
            }
         }
         catch (Exception e)
         {
            throw new RuntimeException("error while reading " + fileName, e);
         }
      }
   }

   private void installScannedComponentAndRoles(Class<Object> scannedClass)
   {
      if (scannedClass.isAnnotationPresent(Name.class))
      {
         addComponentDescriptor(new ComponentDescriptor(scannedClass));
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
      addComponentDescriptor( new ComponentDescriptor( role.name(), scannedClass, scope ) );
   }

   private void addNamespace(Package pkg)
   {
      if (pkg != null)
      {
         Namespace ns = pkg.getAnnotation(Namespace.class);
         if (ns != null)
         {
            log.info("Namespace: " + ns.value() + ", package: " + pkg.getName() + ", prefix: " + ns.prefix());
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
      addNamespace(org.jboss.seam.drools.RuleBase.class.getPackage());
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
      for (ComponentDescriptor component : componentDescriptors.values())
      {
         if ( component.getComponentClass().equals(componentClass) )
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
      addComponent( new ComponentDescriptor(Init.class), context );
      Init init = (Init) Component.getInstance(Init.class, ScopeType.APPLICATION);

      ComponentDescriptor desc = findDescriptor(Jbpm.class);
      if (desc != null && desc.isInstalled())
      {
         init.setJbpmInstalled(true);
      }

      try
      {
         Reflections.classForName("org.jboss.cache.aop.PojoCache");
         addComponentDescriptor( new ComponentDescriptor(PojoCache.class, true) );
      }
      catch (ClassNotFoundException e) {}
      catch (NoClassDefFoundError e) {
         //temp solution due to broken JEMS installer portal profile!
         log.warn("Did not install PojoCache due to NoClassDefFoundError: " + e.getMessage());
      } 

      if (installedComponents.contains(ManagedPersistenceContext.class))
      {
         try
         {
            Reflections.classForName("org.hibernate.Session");
            addComponentDescriptor( new ComponentDescriptor(HibernatePersistenceProvider.class, true) );
         }
         catch (ClassNotFoundException cnfe)
         {
            addComponentDescriptor( new ComponentDescriptor(PersistenceProvider.class, true) );
         }
      }

      if ( init.isDebug() )
      {
         addComponentDescriptor( new ComponentDescriptor(Introspector.class, true) );
         addComponentDescriptor( new ComponentDescriptor(org.jboss.seam.debug.Contexts.class, true) );
      }

      log.info("Installing components...");
      boolean installedSomething = false;
      do
      {
         installedSomething = false;
         for (ComponentDescriptor componentDescriptor : componentDescriptors.values())
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
      String[] classDependencies = descriptor.getClassDependencies();
      if (classDependencies != null) 
      {
          for (String className: classDependencies) 
          {
              try 
              {
                  descriptor.getComponentClass().getClassLoader().loadClass(className);
              } 
              catch (Exception e) 
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
      Component component = new Component(descriptor.getComponentClass(), name, descriptor
               .getScope(), descriptor.getJndiName());
      context.set(componentName, component);
   }

   private static String toCamelCase(String hyphenated, boolean initialUpper)
   {
      StringTokenizer tokens = new StringTokenizer(hyphenated, "-");
      StringBuilder result = new StringBuilder( hyphenated.length() );
      String firstToken = tokens.nextToken();
      if (initialUpper)
      {
         result.append( Character.toUpperCase( firstToken.charAt(0) ) )
         .append( firstToken.substring(1) );         
      }
      else
      {
         result.append(firstToken);
      }
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
      private Integer precedence;

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

      public String[] getClassDependencies() {
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
         return install.value();
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

      @Override
      public String toString()
      {
         return "ComponentDescriptor(" + getName() + ')';
      }
   }

}
