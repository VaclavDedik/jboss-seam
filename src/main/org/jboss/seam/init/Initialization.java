/*
 * JBoss, Home of Professional Open Source
 * 
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.seam.init;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
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
import java.util.TreeSet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

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
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Jbpm;
import org.jboss.seam.core.PojoCache;
import org.jboss.seam.deployment.ComponentScanner;
import org.jboss.seam.deployment.NamespaceScanner;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
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
   private Map<String, Set<ComponentDescriptor>> componentDescriptors = new HashMap<String, Set<ComponentDescriptor>>();
   private List<FactoryDescriptor> factoryDescriptors = new ArrayList<FactoryDescriptor>();
   private Set<Class> installedComponentClasses = new HashSet<Class>();
   private Set<String> importedPackages = new HashSet<String>();
   private Map<String, NamespaceDescriptor> namespaceMap = new HashMap<String, NamespaceDescriptor>();
   private final Map<String, EventListenerDescriptor> eventListenerDescriptors = new HashMap<String, EventListenerDescriptor>();

   public Initialization(ServletContext servletContext)
   {
      this.servletContext = servletContext;
   }
   
   public Initialization create()
   {
      addNamespaces();
      initComponentsFromXmlDocument("/WEB-INF/components.xml");
      initComponentsFromXmlDocument("/WEB-INF/events.xml"); //deprecated
      initComponentsFromXmlDocuments();
      initPropertiesFromServletContext();
      initPropertiesFromResource();
      initJndiProperties();
      return this;
   }

   private void initComponentsFromXmlDocuments()
   {
      Enumeration<URL> resources;
      try
      {
         resources = Thread.currentThread().getContextClassLoader().getResources("META-INF/components.xml");
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

   private void initComponentsFromXmlDocument(String resource)
   {
      InputStream stream = Resources.getResourceAsStream(resource, servletContext);
      if (stream != null)
      {
         log.info("reading " + resource);
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

   @SuppressWarnings("unchecked")
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

      List<Element> elements = rootElement.elements("event");
      for (Element event: elements)
      {
         installEventListenerFromXmlElement(event);
      }
      
      for (Element elem : (List<Element>) rootElement.elements())
      {
         String ns = elem.getNamespace().getURI();
         NamespaceDescriptor nsInfo = namespaceMap.get(ns);
         if (nsInfo != null)
         {
            String name = elem.attributeValue("name");
            String elemName = toCamelCase( elem.getName(), true );
            
            String className = nsInfo.getPackage().getName() + '.' + elemName;
            try
            {
               //get the class implied by the namespaced XML element name
               Class<Object> clazz = Reflections.classForName(className);
               Name nameAnnotation = clazz.getAnnotation(Name.class);
               
               //if the name attribute is not explicitly specified in the XML,
               //imply the name from the @Name annotation on the class implied
               //by the XML element name
               if (name == null && nameAnnotation!=null) 
               {
                  name = nameAnnotation.value();
               }
               
               //if this class already has the @Name annotation, the XML element 
               //is just adding configuration to the existing component, don't
               //add another ComponentDescriptor (this is super-important to
               //allow overriding!)
               if ( nameAnnotation!=null && nameAnnotation.value().equals(name) )
               {
                  Install install = clazz.getAnnotation(Install.class);
                  if ( install == null || install.value() )
                  {
                     className = null;
                  }
               }
            }
            catch (ClassNotFoundException cnfe)
            {
               //there is no class implied by the XML element name so the
               //component must be defined some other way, assume that we are
               //just adding configuration, don't add a ComponentDescriptor 
               className = null;
            }

            //finally, if we could not get the name from the XML name attribute,
            //or from an @Name annotation on the class, imply it
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

   @SuppressWarnings("unchecked")
   private void installEventListenerFromXmlElement(Element event)
   {
      String type = event.attributeValue("type");
      if (type==null)
      {
         throw new IllegalArgumentException("must specify type for <event/> declaration");
      }
      EventListenerDescriptor eventListener = eventListenerDescriptors.get(type);
      if (eventListener==null) 
      {
         eventListener = new EventListenerDescriptor(type);
         eventListenerDescriptors.put(type, eventListener);
      }
      
      List<Element> actions = event.elements("action");
      for (Element action: actions)
      {
         String actionExpression = action.attributeValue("expression");
         if (actionExpression==null)
         {
            throw new IllegalArgumentException("must specify expression for <action/> declaration");
         }
         eventListener.getListenerMethodBindings().add(actionExpression);
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

   @SuppressWarnings("unchecked")
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
         installedComponentClasses.add(clazz);
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
      Set<ComponentDescriptor> set = componentDescriptors.get(name);
      if (set==null)
      {
         set = new TreeSet<ComponentDescriptor>(new ComponentDescriptor.PrecedenceComparator());
         componentDescriptors.put(name, set);
      }
      if ( !set.isEmpty() )
      {
         log.info("two components with same name, higher precedence wins: " + name);
      }
      if ( !set.add(descriptor) )
      {
         throw new IllegalStateException("Two components with the same name and precedence: " + name);
      }
   }

   private Conversions.PropertyValue getPropertyValue(Attribute prop, Properties replacements)
   {
      return new Conversions.FlatPropertyValue( trimmedText(prop, replacements) );
   }
   
   @SuppressWarnings("unchecked")
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
      Lifecycle.beginInitialization(servletContext);
      Contexts.getApplicationContext().set(Component.PROPERTIES, properties);
      RedeployableStrategy redeployStrategy = getRedeployableInitialization();
      scanForHotDeployableComponents(redeployStrategy);
      scanForComponents();
      addComponent( new ComponentDescriptor(Init.class), Contexts.getApplicationContext(), redeployStrategy );
      Init init = (Init) Component.getInstance(Init.class, ScopeType.APPLICATION);    
      ComponentDescriptor desc = findDescriptor(Jbpm.class);
      if (desc != null && desc.isInstalled())
      {
         init.setJbpmInstalled(true);
      }
      init.setTimestamp( System.currentTimeMillis() );
      init.setHotDeployPaths( redeployStrategy.getPaths() );
      
      addSpecialComponents(init);
      installComponents(init, redeployStrategy);
      
      Lifecycle.endInitialization();
      log.info("done initializing Seam");
      return this;
   }

   public Initialization redeploy(HttpSession session)
   {
      log.info("redeploying");
      Lifecycle.beginReinitialization(servletContext, session);
      Init init = Init.instance();
      for ( String name: init.getHotDeployableComponents() )
      {
         Component component = Component.forName(name);
         if (component!=null)
         {
            ScopeType scope = component.getScope();
            if ( scope!=ScopeType.STATELESS && scope.isContextActive() )
            {
               scope.getContext().remove(name);
            }
         }
         Contexts.getApplicationContext().remove(name + ".component");
      }
      //TODO open the ability to reuse the classloader by looking at the components class classloaders
      RedeployableStrategy redeployStrategy = getRedeployableInitialization();
      scanForHotDeployableComponents(redeployStrategy);
      init.setTimestamp( System.currentTimeMillis() );
      init.setHotDeployPaths(redeployStrategy.getPaths());
      installComponents(init, redeployStrategy);
      Lifecycle.endInitialization();
      log.info("done redeploying");
      return this;
   }

   private RedeployableStrategy getRedeployableInitialization() 
   {
      File hotDeployDir = new File( servletContext.getRealPath("/WEB-INF/dev") );
      String strategy = getRedeployableStrategyName(hotDeployDir);
      try
      {
         Class initializer = Reflections.classForName(strategy);
         Constructor ctr = initializer.getConstructor(File.class);
         return (RedeployableStrategy) ctr.newInstance(hotDeployDir);
      }
      catch (Exception e)
      {
         throw new RuntimeException( "Unable to instantiate redeployable strategy: " + strategy );
      }
   }

   private String getRedeployableStrategyName(File hotDeployDir)
   {
      boolean debugEnabled = Thread.currentThread().getContextClassLoader()
                  .getResource("META-INF/debug.xhtml")!=null;
      boolean isGroovy;
      try 
      {
         Reflections.classForName( "groovy.lang.GroovyObject" );
         isGroovy = true;
      }
      catch (ClassNotFoundException e)
      {
         //groovy is not there
         isGroovy = false;
      }

      if ( debugEnabled && hotDeployDir.exists() )
      {
         if (isGroovy)
         {
            log.debug("Using Java+Groovy hot deploy");
            return "org.jboss.seam.init.GroovyHotRedeployable";
         }
         else 
         {
            log.debug("Using Java hot deploy");
            return "org.jboss.seam.init.JavaHotRedeployable";
         }
      }
//    else if (isGroovy) {
//       TODO Implement it even when debug is not set up
//    }
      else 
      {
         log.debug("No hot deploy used");
         return "org.jboss.seam.init.NoHotRedeployable";
      }
   }

   private void scanForHotDeployableComponents(RedeployableStrategy redeployStrategy)
   {
      ComponentScanner scanner = redeployStrategy.getScanner();
      if (scanner != null) 
      {
         Set<Class<Object>> scannedClasses = new HashSet<Class<Object>>();
         scannedClasses.addAll(scanner.getClasses());
         Set<Package> scannedPackages = new HashSet<Package>();
         for (Class<Object> scannedClass: scannedClasses)
         {
            installScannedClass(scannedPackages, scannedClass);
         }
      }
   }

   private void scanForComponents()
   {
      Set<Class<Object>> scannedClasses = new HashSet<Class<Object>>();
      scannedClasses.addAll( new ComponentScanner("seam.properties").getClasses() );
      scannedClasses.addAll( new ComponentScanner("META-INF/seam.properties").getClasses() );
      scannedClasses.addAll( new ComponentScanner("META-INF/components.xml").getClasses() );

      Set<Package> scannedPackages = new HashSet<Package>();
      for (Class<Object> scannedClass: scannedClasses)
      {
         installScannedClass(scannedPackages, scannedClass);
      }
   }

   private void installScannedClass(Set<Package> scannedPackages, Class<Object> scannedClass)
   {
      installScannedComponentAndRoles(scannedClass);
      installComponentsFromDescriptor( classDescriptorFilename(scannedClass), scannedClass );
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
            namespaceMap.put(ns.value(), new NamespaceDescriptor(ns, pkg));
         }
      }
   }

   private void addNamespaces()
   {
      for ( Package pkg : new NamespaceScanner("META-INF/components.xml").getPackages() )
      {
         addNamespace(pkg);
      }
      for ( Package pkg : new NamespaceScanner("seam.properties").getPackages() )
      {
         addNamespace(pkg);
      }
      for ( Package pkg : new NamespaceScanner("META-INF/seam.properties").getPackages() )
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
      for (Set<ComponentDescriptor> components : componentDescriptors.values())
      {
         for (ComponentDescriptor component: components)
         {
            if ( component.getComponentClass().equals(componentClass) )
            {
               return component;
            }
         }
      }
      return null;
   }

   private void addSpecialComponents(Init init)
   {
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
   }

   private void installComponents(Init init, RedeployableStrategy redeployStrategy)
   {
      log.info("Installing components...");
      Context context = Contexts.getApplicationContext();

      DependencyManager manager = new DependencyManager(componentDescriptors);

      Set<ComponentDescriptor> installable = manager.installedSet();      
      for (ComponentDescriptor componentDescriptor: installable) {
          String compName = componentDescriptor.getName() + COMPONENT_SUFFIX;

          if (!context.isSet(compName)) {
              addComponent(componentDescriptor, context, redeployStrategy);

              if (componentDescriptor.isAutoCreate()) {
                  init.addAutocreateVariable( componentDescriptor.getName() );
              }

              if (componentDescriptor.isFilter()) {
                  init.addInstalledFilter( componentDescriptor.getName() );
              }

              if (componentDescriptor.isResourceProvider()) {
                  init.addResourceProvider( componentDescriptor.getName() );
              }
          }
      }


      for (FactoryDescriptor factoryDescriptor : factoryDescriptors)
      {
         if (factoryDescriptor.isValueBinding())
         {
            init.addFactoryValueExpression(factoryDescriptor.getName(), factoryDescriptor.getValue(),
                     factoryDescriptor.getScope());
         }
         else
         {
            init.addFactoryMethodExpression(factoryDescriptor.getName(),
                     factoryDescriptor.getMethod(), factoryDescriptor.getScope());
         }
         if (factoryDescriptor.isAutoCreate())
         {
            init.addAutocreateVariable(factoryDescriptor.getName());
         }
      }
      
      for (EventListenerDescriptor listenerDescriptor: eventListenerDescriptors.values())
      {
         for (String expression: listenerDescriptor.getListenerMethodBindings())
         {
            init.addObserverMethodExpression( listenerDescriptor.getType(), Expressions.instance().createMethodExpression(expression) );
         }
      }
   }

  
   /**
    * This actually creates a real Component and should only be called when
    * we want to install a component
    */
   protected void addComponent(ComponentDescriptor descriptor, Context context, RedeployableStrategy redeployStrategy)
   {
      String name = descriptor.getName();
      String componentName = name + COMPONENT_SUFFIX;
      try
      {
         Component component = new Component(
               descriptor.getComponentClass(), 
               name, 
               descriptor.getScope(), 
               descriptor.getJndiName()
            );
         context.set(componentName, component);
         if ( redeployStrategy.isFromHotDeployClassLoader( descriptor.getComponentClass() ) )
         {
            Init.instance().addHotDeployableComponent( component.getName() );
         }
      }
      catch (Throwable e)
      {
         throw new RuntimeException("Could not create Component: " + name, e);
      }      
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

   
   
   private static class EventListenerDescriptor
   {
      private String type;
      private List<String> listenerMethodBindings = new ArrayList<String>();
      
      EventListenerDescriptor(String type)
      {
         this.type = type;
      }
      
      public String getType()
      {
         return type;
      }

      public List<String> getListenerMethodBindings()
      {
         return listenerMethodBindings;
      }
      
      @Override
      public String toString()
      {
         return "EventListenerDescriptor(" + type + ')';
      }
   }

   
}
