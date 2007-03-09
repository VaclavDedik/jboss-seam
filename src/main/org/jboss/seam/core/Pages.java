package org.jboss.seam.core;
import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions.MethodBinding;
import org.jboss.seam.core.Expressions.ValueBinding;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.pages.Action;
import org.jboss.seam.pages.ConversationControl;
import org.jboss.seam.pages.ConversationIdParameter;
import org.jboss.seam.pages.ELConversationIdParameter;
import org.jboss.seam.pages.Input;
import org.jboss.seam.pages.Navigation;
import org.jboss.seam.pages.Output;
import org.jboss.seam.pages.Page;
import org.jboss.seam.pages.Param;
import org.jboss.seam.pages.ProcessControl;
import org.jboss.seam.pages.RedirectNavigationHandler;
import org.jboss.seam.pages.RenderNavigationHandler;
import org.jboss.seam.pages.Rule;
import org.jboss.seam.pages.TaskControl;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.NotLoggedInException;
import org.jboss.seam.util.Parameters;
import org.jboss.seam.util.Resources;
import org.jboss.seam.util.Strings;
import org.jboss.seam.util.XML;
/**
 * Holds metadata for pages defined in pages.xml, including
 * page actions and page descriptions.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Name("org.jboss.seam.core.pages")
@Install(precedence=BUILT_IN)
public class Pages
{   
   private static final LogProvider log = Logging.getLogProvider(Pages.class);
   
   private Map<String, Page> pagesByViewId = Collections.synchronizedMap( new HashMap<String, Page>() );   
   private Map<String, List<Page>> pageStacksByViewId = Collections.synchronizedMap( new HashMap<String, List<Page>>() );   
   private String noConversationViewId;
   private String loginViewId;
   private Map<String, ConversationIdParameter> conversations = Collections.synchronizedMap( new HashMap<String, ConversationIdParameter>() );
 
   private SortedSet<String> wildcardViewIds = new TreeSet<String>( 
         new Comparator<String>() {
            public int compare(String x, String y)
            {
               if ( x.length()<y.length() ) return -1;
               if ( x.length()> y.length() ) return 1;
               return x.compareTo(y);
            }
         } 
      );
   
   @Create
   public void initialize()
   {
      InputStream stream = Resources.getResourceAsStream("/WEB-INF/pages.xml");      
      if (stream==null)
      {
         log.info("no pages.xml file found");
      }
      else
      {
         log.info("reading pages.xml");
         parse(stream);
      }
   }
   /**
    * Run any navigation rule defined in pages.xml
    * 
    * @param actionExpression the action method binding expression
    * @param actionOutcomeValue the outcome of the action method
    * @return true if a navigation rule was found
    */
   public boolean navigate(FacesContext context, String actionExpression, String actionOutcomeValue)
   {
      String viewId = context.getViewRoot().getViewId();
      if (viewId!=null)
      {
         List<Page> stack = getPageStack(viewId);
         for (int i=stack.size()-1; i>=0; i--)
         {
            Page page = stack.get(i);
            Navigation navigation = page.getNavigations().get(actionExpression);
            if (navigation==null)
            {
               navigation = page.getDefaultNavigation();
            }
            
            if ( navigation!=null && navigation.navigate(context, actionOutcomeValue) ) return true;  
            
         }
      }
      return false;
   }
   /**
    * Get the Page object for the given view id.
    * 
    * @param viewId a JSF view id
    */
   public Page getPage(String viewId)
   {
      if (viewId==null)
      {
         //for tests
         return new Page(viewId);
      }
      else
      {
         Page result = getCachedPage(viewId);
         if (result==null)
         {
            return createPage(viewId);
         }
         else
         {
            return result;
         }
      }
   }
   /**
    * Create a new Page object for a JSF view id,
    * by searching for a viewId.page.xml file.
    */
   private Page createPage(String viewId)
   {
      String resourceName = replaceExtension(viewId, ".page.xml");
      InputStream stream = resourceName==null ? 
            null : Resources.getResourceAsStream( resourceName.substring(1) );
      if ( stream==null ) 
      {
         Page result = new Page(viewId);
         pagesByViewId.put(viewId, result);
         return result;
      }
      else
      {
         parse(stream, viewId);
         return getCachedPage(viewId);
      }
   }
   private Page getCachedPage(String viewId)
   {
      Page result = pagesByViewId.get(viewId);
      if (result==null)
      {
         //workaround for what I believe is a bug in the JSF RI
         viewId = replaceExtension( viewId, getSuffix() );
         if (viewId!=null)
         {
            result = pagesByViewId.get( viewId );
         }
      }
      return result;
   }
   
   private static String replaceExtension(String viewId, String suffix)
   {
      int loc = viewId.lastIndexOf('.');
      return loc<0 ? null : viewId.substring(0, loc) + suffix;
   }
   
   /**
    * Get the stack of Page objects, from least specific to 
    * most specific, that match the given view id.
    * 
    * @param viewId a JSF view id
    */
   protected List<Page> getPageStack(String viewId)
   {
      List<Page> stack = pageStacksByViewId.get(viewId);
      if (stack==null)
      {
         stack = createPageStack(viewId);
         pageStacksByViewId.put(viewId, stack);
      }
      return stack;
   }
   /**
    * Create the stack of pages that match a JSF view id
    */
   private List<Page> createPageStack(String viewId)
   {
      List<Page> stack = new ArrayList<Page>(1);
      if (viewId!=null)
      {
         for (String wildcard: wildcardViewIds)
         {
            if ( viewId.startsWith( wildcard.substring(0, wildcard.length()-1) ) )
            {
               stack.add( getPage(wildcard) );
            }
         }
      }
      Page page = getPage(viewId);
      if (page!=null) stack.add(page);
      return stack;
   }
   
   /**
    * Call page actions, and validate the existence of a conversation
    * for pages which require a long-running conversation, starting
    * with the most general view id, ending at the most specific
    */
   public boolean enterPage(FacesContext facesContext)
   {
      boolean result = false;
      String viewId = facesContext.getViewRoot().getViewId();
      
      String requestScheme = getRequestScheme(facesContext);
      if ( requestScheme!=null )
      {
         String scheme = getScheme(viewId);
         if ( scheme!=null && !requestScheme.equals(scheme) )
         {
            Manager.instance().redirect(viewId);              
            return result;
         }
      }
      
      for ( Page page: getPageStack(viewId) )
      {         
         if ( isNoConversationRedirectRequired(page) )
         {
            redirectToNoConversationView();
            return result;
         }
         else if ( isLoginRedirectRequired(viewId, page) )
         {
            redirectToLoginView();
            return result;
         }
         else
         {
            result = page.enter(facesContext) || result;
         }
      }
      
      //run the s:link / s:button action after checking the
      //conversation existence!
      result = callAction(facesContext) || result;
      return result;
   }
   
   private boolean isNoConversationRedirectRequired(Page page)
   {
      return page.isConversationRequired() && 
            !Manager.instance().isLongRunningConversation();
   }
   
   private boolean isLoginRedirectRequired(String viewId, Page page)
   {
      return page.isLoginRequired() && 
            !viewId.equals( getLoginViewId() ) && 
            !Identity.instance().isLoggedIn();
   }
   
   private static String getRequestScheme(FacesContext facesContext)
   {
      String requestUrl = getRequestUrl(facesContext);
      if (requestUrl==null)
      {
         return null;
      }
      else
      {
         int idx = requestUrl.indexOf(':');
         return idx<0 ? null : requestUrl.substring(0, idx);
      }
   }
   
   public String encodeScheme(String viewId, FacesContext context, String url)
   {
      String scheme = getScheme(viewId);
      if (scheme != null)
      {
         String requestUrl = getRequestUrl(context);
         if (requestUrl!=null)
         {
            try
            {
               URL serverUrl = new URL(requestUrl);
               
               StringBuilder sb = new StringBuilder();
               sb.append(scheme);
               sb.append("://");
               sb.append(serverUrl.getHost());
               if (serverUrl.getPort() != -1)
               {
                  sb.append(":");
                  sb.append(serverUrl.getPort());
               }
               
               if (!url.startsWith("/")) sb.append("/");
               
               sb.append(url);
               
               url = sb.toString();
            }
            catch (MalformedURLException ex) 
            {
               throw new RuntimeException(ex);
            }
         }
      }
      return url;   
   }
   
   private static String getRequestUrl(FacesContext facesContext)
   {
      Object request = facesContext.getExternalContext().getRequest(); 
      if (request instanceof HttpServletRequest) 
      {
         return ( (HttpServletRequest) request).getRequestURL().toString();
      }
      else
      {
         return null;
      }
   }
   
   public void redirectToLoginView()
   {
      notLoggedIn();
      
      String loginViewId = getLoginViewId();
      if (loginViewId==null)
      {
         throw new NotLoggedInException();
      }
      else
      {
         Manager.instance().redirect(loginViewId);
      }
   }
   
   public void redirectToNoConversationView()
   {
      noConversation();
      
      //stuff from jPDL takes precedence
      org.jboss.seam.core.FacesPage facesPage = org.jboss.seam.core.FacesPage.instance();
      String pageflowName = facesPage.getPageflowName();
      String pageflowNodeName = facesPage.getPageflowNodeName();
      
      String noConversationViewId = null;
      if (pageflowName==null || pageflowNodeName==null)
      {
         String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
         noConversationViewId = getNoConversationViewId(viewId);
      }
      else
      {
         noConversationViewId = Pageflow.instance().getNoConversationViewId(pageflowName, pageflowNodeName);
      }
      
      if (noConversationViewId!=null)
      {
         Manager.instance().redirect(noConversationViewId);
      }
   }
   
   public String getScheme(String viewId)
   {
      List<Page> stack = getPageStack(viewId);
      for ( int i = stack.size() - 1; i >= 0; i-- )
      {
         Page page = stack.get(i);
         if (page.getScheme() != null) return page.getScheme();
      }
      return null;
   }   

   protected void noConversation()
   {
      Events.instance().raiseEvent("org.jboss.seam.noConversation");
      
      FacesMessages.instance().addFromResourceBundleOrDefault( 
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.NoConversation", 
            "The conversation ended, timed out or was processing another request" 
         );
   }

   protected void notLoggedIn()
   {
      Events.instance().raiseEvent("org.jboss.seam.notLoggedIn");
      
      FacesMessages.instance().addFromResourceBundleOrDefault( 
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.NotLoggedIn", 
            "Please log in first" 
         );
   }

   public static String toString(Object returnValue)
   {
      return returnValue == null ? null : returnValue.toString();
   }
   
   /**
    * Call the JSF navigation handler
    */
   public static void handleOutcome(FacesContext facesContext, String outcome, String fromAction)
   {
      facesContext.getApplication().getNavigationHandler()
            .handleNavigation(facesContext, fromAction, outcome);
      //after every time that the view may have changed,
      //we need to flush the page context, since the 
      //attribute map is being discarder
      Contexts.getPageContext().flush();
   }
   
   public static Pages instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application context");
      }
      return (Pages) Component.getInstance(Pages.class, ScopeType.APPLICATION);
   }
   /**
    * Call the action requested by s:link or s:button.
    */
   private static boolean callAction(FacesContext facesContext)
   {
      //TODO: refactor with Pages.instance().callAction()!!
      
      boolean result = false;
      
      String outcome = (String) facesContext.getExternalContext()
            .getRequestParameterMap()
            .get("actionOutcome");
      String fromAction = outcome;
      
      if (outcome==null)
      {
         String actionId = (String) facesContext.getExternalContext()
               .getRequestParameterMap()
               .get("actionMethod");
         if (actionId!=null)
         {
            if ( !SafeActions.instance().isActionSafe(actionId) ) return result;
            String expression = SafeActions.toAction(actionId);
            result = true;
            MethodBinding actionBinding = Expressions.instance().createMethodBinding(expression);
            outcome = toString( actionBinding.invoke() );
            fromAction = expression;
            handleOutcome(facesContext, outcome, fromAction);
         }
      }
      else
      {
         handleOutcome(facesContext, outcome, fromAction);
      }
      
      return result;
   }
   
   /**
    * Build a list of page-scoped resource bundles, from most
    * specific view id, to most general.
    */
   public List<ResourceBundle> getResourceBundles(String viewId)
   {
      List<ResourceBundle> result = new ArrayList<ResourceBundle>(1);
      List<Page> stack = getPageStack(viewId);
      for (int i=stack.size()-1; i>=0; i--)
      {
         Page page = stack.get(i);
         ResourceBundle bundle = page.getResourceBundle();
         if ( bundle!=null ) result.add(bundle);
      }
      return result;
   }
   
   /**
    * Get the values of any page parameters by evaluating the value bindings
    * against the model and converting to String.
    * 
    * @param viewId the JSF view id
    * @return a map of page parameter name to String value
    */
   public Map<String, Object> getConvertedParameters(FacesContext facesContext, String viewId)
   {
      return getConvertedParameters(facesContext, viewId, Collections.EMPTY_SET);
   }
   
   /**
    * Get the values of any page parameters by evaluating the value bindings
    * against the model.
    * 
    * @param viewId the JSF view id
    * @return a map of page parameter name to value
    */
   protected Map<String, Object> getParameters(String viewId)
   {
      Map<String, Object> parameters = new HashMap<String, Object>();
      for ( Page page: getPageStack(viewId) )
      {
         for ( Param pageParameter: page.getParameters() )
         {
            ValueBinding valueBinding = pageParameter.getValueBinding();
            Object value;
            if (valueBinding==null)
            {
               value = Contexts.getPageContext().get( pageParameter.getName() );
            }
            else
            {
               value = valueBinding.getValue();
            }
            if (value!=null)
            {
               parameters.put( pageParameter.getName(), value );
            }
         }
      }
      return parameters;
   }
   
   /**
    * Get the values of any page parameters by evaluating the value bindings
    * against the model and converting to String.
    * 
    * @param viewId the JSF view id
    * @param overridden excluded parameters
    * @return a map of page parameter name to String value
    */
   public Map<String, Object> getConvertedParameters(FacesContext facesContext, String viewId, Set<String> overridden)
   {
      Map<String, Object> parameters = new HashMap<String, Object>();
      for ( Page page: getPageStack(viewId) )
      {
         for ( Param pageParameter: page.getParameters() )
         {
            if ( !overridden.contains( pageParameter.getName() ) )
            {
               Object value = getPageParameterValue(facesContext, pageParameter);
               if (value!=null) 
               {
                  parameters.put( pageParameter.getName(), value );
               }
            }
         }
      }
      return parameters;
   }
   
   /**
    * Get the current value of a page parameter, looking in the page context
    * if there is no value binding
    */
   private Object getPageParameterValue(FacesContext facesContext, Param pageParameter)
   {
      ValueBinding valueBinding = pageParameter.getValueBinding();
      if (valueBinding==null)
      {
         return Contexts.getPageContext().get( pageParameter.getName() );
      }
      else
      {
         return pageParameter.getValueFromModel(facesContext);
      }
   }
   
   /**
    * Apply any page parameters passed as parameter values to the model.
    */
   public void applyRequestParameterValues(FacesContext facesContext)
   {
      String viewId = facesContext.getViewRoot().getViewId();
      Map<String, String[]> requestParameters = Parameters.getRequestParameters();
      for ( Page page: getPageStack(viewId) )
      {
         for ( Param pageParameter: page.getParameters() )
         {  
            
            Object value = pageParameter.getValueFromRequest(facesContext, requestParameters);
            if (value!=null)
            {
               ValueBinding valueBinding = pageParameter.getValueBinding();
               if (valueBinding==null)
               {
                  Contexts.getPageContext().set( pageParameter.getName(), value );
               }
               else
               {
                  valueBinding.setValue(value);
               }
            }
         }
      }
   }
   
   /**
    * Apply any page parameters passed as view root attributes to the model.
    */
   public void applyViewRootValues(FacesContext facesContext)
   {
      String viewId = facesContext.getViewRoot().getViewId();
      for ( Page page: getPageStack(viewId) )
      {
         for ( Param pageParameter: page.getParameters() )
         {         
            ValueBinding valueBinding = pageParameter.getValueBinding();
            if (valueBinding!=null)
            {
               Object object = Contexts.getPageContext().get( pageParameter.getName() );
               if (object!=null)
               {
                  valueBinding.setValue(object);
               }
            }
         }
      }
   }

   public Map<String, Object> getViewRootValues(FacesContext facesContext)
   {
      Map<String, Object> parameters = new HashMap<String, Object>();
      String viewId = facesContext.getViewRoot().getViewId();
      for ( Page page: getPageStack(viewId) )
      {
         for ( Param pageParameter: page.getParameters() )
         {
            Object object = Contexts.getPageContext().get( pageParameter.getName() );
            if (object!=null)
            {
               parameters.put(  pageParameter.getName(), object );
            }
         }
      }
      return parameters;
   }
   
   /**
    * The global setting for no-conversation-viewid.
    * 
    * @return a JSF view id
    */
   public String getNoConversationViewId()
   {
      return noConversationViewId;
   }
   public void setNoConversationViewId(String noConversationViewId)
   {
      this.noConversationViewId = noConversationViewId;
   }
   
   /**
    * Encode page parameters into a URL
    * 
    * @param url the base URL
    * @param viewId the JSF view id of the page
    * @return the URL with parameters appended
    */
   public String encodePageParameters(FacesContext facesContext, String url, String viewId)
   {
      return encodePageParameters(facesContext, url, viewId, Collections.EMPTY_SET);
   }
   
   /**
    * Encode page parameters into a URL
    * 
    * @param url the base URL
    * @param viewId the JSF view id of the page
    * @param overridden excluded parameters
    * @return the URL with parameters appended
    */
   public String encodePageParameters(FacesContext facesContext, String url, String viewId, Set<String> overridden)
   {
      Map<String, Object> parameters = getConvertedParameters(facesContext, viewId, overridden);
      return Manager.instance().encodeParameters(url, parameters);
   }
   
   /**
    * Store the page parameters to the JSF view root
    */
   public void storePageParameters(FacesContext facesContext)
   {
      String viewId = facesContext.getViewRoot().getViewId();
      for ( Map.Entry<String, Object> param: getParameters(viewId).entrySet() )
      {
         Contexts.getPageContext().set( param.getKey(), param.getValue() );
      }
   }
   
   /**
    * Search for a defined no-conversation-view-id, beginning with
    * the most specific view id, then wildcarded view ids, and 
    * finally the global setting
    */
   public String getNoConversationViewId(String viewId)
   {
      List<Page> stack = getPageStack(viewId);
      for (int i=stack.size()-1; i>=0; i--)
      {
         Page page = stack.get(i);
         String noConversationViewId = page.getNoConversationViewId();
         if (noConversationViewId!=null)
         {
            return noConversationViewId;
         }
      }
      return this.noConversationViewId;
   }
   
   /**
    * Search for a defined conversation timeout, beginning with
    * the most specific view id, then wildcarded view ids, and 
    * finally the global setting from Manager
    */
   public Integer getTimeout(String viewId)
   {
      List<Page> stack = getPageStack(viewId);
      for (int i=stack.size()-1; i>=0; i--)
      {
         Page page = stack.get(i);
         Integer timeout = page.getTimeout();
         if (timeout!=null)
         {
            return timeout;
         }
      }
      return Manager.instance().getConversationTimeout();
   }
   
   public static String getSuffix()
   {
      String defaultSuffix = FacesContext.getCurrentInstance().getExternalContext()
            .getInitParameter(ViewHandler.DEFAULT_SUFFIX_PARAM_NAME);
      return defaultSuffix == null ? ViewHandler.DEFAULT_SUFFIX : defaultSuffix;
   }
   
   /**
    * Parse a pages.xml file
    */
   private void parse(InputStream stream)
   {
      Element root = getDocumentRoot(stream);
      if (noConversationViewId==null) //let the setting in components.xml override the pages.xml
      {
         noConversationViewId = root.attributeValue("no-conversation-view-id");
      }
      if (loginViewId==null) //let the setting in components.xml override the pages.xml
      {
         loginViewId = root.attributeValue("login-view-id");
      }
      
      List<Element> elements = root.elements("conversation");
      for (Element conversation : elements)
      {
         parseConversation(conversation, conversation.attributeValue("name"));
      }
      
      elements = root.elements("page");
      for (Element page: elements)
      {
         parse( page, page.attributeValue("view-id") );
      } 
   }
   
   /**
    * Parse a viewId.page.xml file
    */
   private void parse(InputStream stream, String viewId)
   {
      parse( getDocumentRoot(stream), viewId );
   }
   
   /**
    * Get the root element of the document
    */
   private static Element getDocumentRoot(InputStream stream)
   {
      try
      {
         return XML.getRootElement(stream);
      }
      catch (DocumentException de)
      {
         throw new RuntimeException(de);
      }
   }
   
   private void parseConversation(Element element, String name)
   {
      if (name == null)
      {
         throw new IllegalStateException("Must specify name for <conversation/> declaration");
      }
      
      if (conversations.containsKey(name))
      {
         throw new IllegalStateException("<conversation/> declaration already exists for [" + name + "]");
      }
      
      ELConversationIdParameter param = new ELConversationIdParameter(name, 
               element.attributeValue("parameter-name"), 
               element.attributeValue("parameter-value"));
      
      conversations.put(name, param);
   }
   
   /**
    * Parse a page element and add a Page to the map
    */
   private void parse(Element element, String viewId)
   {
      if (viewId==null)
      {
         throw new IllegalStateException("Must specify view-id for <page/> declaration");
      }
      
      if ( viewId.endsWith("*") )
      {
         wildcardViewIds.add(viewId);
      }
      Page page = new Page(viewId);
      pagesByViewId.put(viewId, page);
      
      parsePage(page, element, viewId);
      parseConversationControl( element, page.getConversationControl() );
      parseTaskControl(element, page.getTaskControl());
      parseProcessControl(element, page.getProcessControl());
      List<Element> children = element.elements("param");
      for (Element param: children)
      {
         page.getParameters().add( parseParam(param) );
      }
      
      List<Element> moreChildren = element.elements("navigation");
      for (Element fromAction: moreChildren)
      {
         parseActionNavigation(page, fromAction);
      }
      
      Element restrict = element.element("restrict");
      if (restrict != null)
      {
         page.setRestricted(true);
         String expr = restrict.getTextTrim();
         if ( !Strings.isEmpty(expr) ) page.setRestriction(expr);
      }
   }
   /**
    * Parse the attributes of page
    */
   private Page parsePage(Page page, Element element, String viewId)
   {
      
      page.setSwitchEnabled( !"disabled".equals( element.attributeValue("switch") ) );
      
      Element optionalElement = element.element("description");
      String description = optionalElement==null ? 
               element.getTextTrim() : optionalElement.getTextTrim();
      if (description!=null && description.length()>0)
      {
         page.setDescription(description);
      }
      
      String timeoutString = element.attributeValue("timeout");
      if (timeoutString!=null)
      {
         page.setTimeout(Integer.parseInt(timeoutString));
      }
      
      page.setNoConversationViewId( element.attributeValue("no-conversation-view-id") );
      page.setConversationRequired( "true".equals( element.attributeValue("conversation-required") ) );
      page.setLoginRequired( "true".equals( element.attributeValue("login-required") ) );
      page.setScheme( element.attributeValue("scheme") );
      
      ConversationIdParameter param = conversations.get( element.attributeValue("conversation") );
      if (param != null) page.setConversationIdParameter(param);
      
      Action action = parseAction(element, "action");
      if (action!=null) page.getActions().add(action);
      List<Element> childElements = element.elements("action");
      for (Element childElement: childElements)
      {
         page.getActions().add( parseAction(childElement, "execute") );
      }
            
      String bundle = element.attributeValue("bundle");
      if (bundle!=null)
      {
         page.setResourceBundleName(bundle);
      }
      List<Element> moreChildElements = element.elements("in");
      for (Element child: moreChildElements)
      {
         Input input = new Input();
         input.setName( child.attributeValue("name") );
         input.setValue( Expressions.instance().createValueBinding( child.attributeValue("value") ) );
         String scopeName = child.attributeValue("scope");
         if (scopeName!=null)
         {
            input.setScope( ScopeType.valueOf( scopeName.toUpperCase() ) );
         }
         page.getInputs().add(input);
      }
      
      return page;
   }
   
   private static Action parseAction(Element element, String actionAtt)
   {
      Action action = new Action();
      String methodExpression = element.attributeValue(actionAtt);
      if (methodExpression==null) return null;
      if ( methodExpression.startsWith("#{") )
      {
         MethodBinding methodBinding = Expressions.instance().createMethodBinding(methodExpression);
         action.setMethodBinding(methodBinding);
      }
      else
      {
         action.setOutcome(methodExpression);
      }
      String expression = element.attributeValue("if");
      if (expression!=null)
      {
         action.setValueBinding( Expressions.instance().createValueBinding(expression) );
      }
      return action;
   }
   
   /**
    * Parse end-conversation (and end-task) and begin-conversation (start-task and begin-task) 
    *
    */
   private static void parseConversationControl(Element element, ConversationControl control)
   {
      Element endConversation = element.element("end-conversation");
      endConversation = endConversation == null ? element.element("end-task") : endConversation;
      if ( endConversation!=null )
      {
         control.setEndConversation(true);
         control.setEndConversationBeforeRedirect( "true".equals( endConversation.attributeValue("before-redirect") ) );
         String expression = endConversation.attributeValue("if");
         if (expression!=null)
         {
            control.setEndConversationCondition( Expressions.instance().createValueBinding(expression) );
         }
      }
      
      Element beginConversation = element.element("begin-conversation");
      beginConversation = beginConversation == null ? element.element("begin-task") : beginConversation;
      beginConversation = beginConversation == null ? element.element("start-task") : beginConversation;
      if ( beginConversation!=null )
      {
         control.setBeginConversation(true);
         control.setJoin( "true".equals( beginConversation.attributeValue("join") ) );
         control.setNested( "true".equals( beginConversation.attributeValue("nested") ) );
         control.setPageflow( beginConversation.attributeValue("pageflow") );
         String flushMode = beginConversation.attributeValue("flush-mode");
         if (flushMode!=null)
         {
            control.setFlushMode( FlushModeType.valueOf( flushMode.toUpperCase() ) );
         }
         String expression = beginConversation.attributeValue("if");
         if (expression!=null)
         {
            control.setBeginConversationCondition( Expressions.instance().createValueBinding(expression) );
         }
      }
      
      if ( control.isBeginConversation() && control.isEndConversation() )
      {
         throw new IllegalStateException("cannot use both <begin-conversation/> and <end-conversation/>");
      }
   }
   
   /**
    * Parse begin-task, start-task and end-task
    */
   private static void parseTaskControl(Element element, TaskControl control)
   {
      Element endTask = element.element("end-task");
      if ( endTask!=null )
      {
         control.setEndTask(true);
         control.setTransition( endTask.attributeValue("transition") );
      }
      
      Element beginTask = element.element("begin-task");
      if ( beginTask!=null )
      {
         control.setBeginTask(true);
         String taskId = beginTask.attributeValue("task-id");
         if (taskId==null)
         {
           taskId = "#{param.taskId}";
         }
         control.setTaskId( Expressions.instance().createValueBinding(taskId) );
      }
      
      Element startTask = element.element("start-task");
      if ( startTask!=null )
      {
         control.setStartTask(true);
         String taskId = startTask.attributeValue("task-id");
         if (taskId==null)
         {
           taskId = "#{param.taskId}";
         }
         control.setTaskId( Expressions.instance().createValueBinding(taskId) );
      }
      
      if ( control.isBeginTask() && control.isEndTask() )
      {
         throw new IllegalStateException("cannot use both <begin-task/> and <end-task/>");
      }
      else if ( control.isBeginTask() && control.isStartTask() )
      {
          throw new IllegalStateException("cannot use both <start-task/> and <begin-task/>");
       }
      else if ( control.isStartTask() && control.isEndTask() )
      {
           throw new IllegalStateException("cannot use both <start-task/> and <end-task/>");
       }
   }
   
   /**
    * Parse create-process and end-process
    */
   private static void parseProcessControl(Element element, ProcessControl control)
   {
      Element createProcess = element.element("create-process");
      if ( createProcess!=null )
      {
         control.setCreateProcess(true);
         control.setDefinition( createProcess.attributeValue("definition") );
      }
      
      Element resumeProcess = element.element("resume-process");
      if ( resumeProcess!=null )
      {
         control.setResumeProcess(true);
         String processId = resumeProcess.attributeValue("process-id");
         if (processId==null)
         {
           processId = "#{param.processId}";
         }
         control.setProcessId( Expressions.instance().createValueBinding(processId) );
      }
      
      if ( control.isCreateProcess() && control.isResumeProcess() )
      {
         throw new IllegalStateException("cannot use both <create-process/> and <resume-process/>");
      }
   }
   
   /**
    * Parse navigation
    */
   private static void parseActionNavigation(Page entry, Element element)
   {
      Navigation navigation = new Navigation(); 
      String outcomeExpression = element.attributeValue("evaluate");
      if (outcomeExpression!=null)
      {
         navigation.setOutcome( Expressions.instance().createValueBinding(outcomeExpression) );
      }
      
      List<Element> cases = element.elements("rule");
      for (Element childElement: cases)
      {
         navigation.getRules().add( parseRule(childElement) );
      }
      
      Rule rule = new Rule();
      parseNavigationHandler(element, rule);
      parseConversationControl( element, rule.getConversationControl() );
      parseTaskControl(element, rule.getTaskControl());
      parseProcessControl(element, rule.getProcessControl());
      navigation.setRule(rule);
      
      String expression = element.attributeValue("from-action");
      if (expression==null)
      {
         if (entry.getDefaultNavigation()==null)
         {
            entry.setDefaultNavigation(navigation);
         }
         else
         {
            throw new IllegalStateException("multiple catchall <navigation> elements");
         }
      }
      else
      {
         Object old = entry.getNavigations().put(expression, navigation);
         if (old!=null)
         {
            throw new IllegalStateException("multiple <navigation> elements for action: " + expression);
         }
      }
   }
   
   /**
    * Parse param
    */
   private static Param parseParam(Element element)
   {
      String valueExpression = element.attributeValue("value");
      String name = element.attributeValue("name");
      if (name==null)
      {
         if (valueExpression==null)
         {
            throw new IllegalArgumentException("must specify name or value for page <param/> declaration");
         }
         name = valueExpression.substring(2, valueExpression.length()-1);
      }
      Param param = new Param(name);
      if (valueExpression!=null)
      {
         param.setValueBinding(Expressions.instance().createValueBinding(valueExpression));
      }
      param.setConverterId(element.attributeValue("converterId"));
      String converterExpression = element.attributeValue("converter");
      if (converterExpression!=null)
      {
         param.setConverterValueBinding(Expressions.instance().createValueBinding(converterExpression));
      }
      return param;
   }
   
   /**
    * Parse rule
    */
   private static Rule parseRule(Element element)
   {
      Rule rule = new Rule();
      
      rule.setOutcomeValue( element.attributeValue("if-outcome") );
      String expression = element.attributeValue("if");
      if (expression!=null)
      {
         rule.setCondition( Expressions.instance().createValueBinding(expression)  );
      }
      
      parseConversationControl( element, rule.getConversationControl() );
      parseTaskControl(element, rule.getTaskControl());
      parseProcessControl(element, rule.getProcessControl());
      parseNavigationHandler(element, rule);
      
      return rule;
   }
   
   private static void parseNavigationHandler(Element element, Rule rule)
   {
      
      Element render = element.element("render");
      if (render!=null)
      {
         final String viewId = render.attributeValue("view-id");
         Element messageElement = render.element("message");
         String message = messageElement==null ? null : messageElement.getTextTrim();
         String severityName = messageElement==null ? null : messageElement.attributeValue("severity");
         Severity severity = severityName==null ? 
                  FacesMessage.SEVERITY_INFO : 
                  getFacesMessageValuesMap().get( severityName.toUpperCase() );
         rule.addNavigationHandler( new RenderNavigationHandler(viewId, message, severity) );
      }
      
      Element redirect = element.element("redirect");
      if (redirect!=null)
      {
         List<Element> children = redirect.elements("param");
         final List<Param> params = new ArrayList<Param>();
         for (Element child: children)
         {
            params.add( parseParam(child) );
         }
         final String viewId = redirect.attributeValue("view-id");
         Element messageElement = redirect.element("message");
         String message = messageElement==null ? null : messageElement.getTextTrim();
         String severityName = messageElement==null ? null : messageElement.attributeValue("severity");
         Severity severity = severityName==null ? 
                  FacesMessage.SEVERITY_INFO : 
                  getFacesMessageValuesMap().get( severityName.toUpperCase() );
         rule.addNavigationHandler( new RedirectNavigationHandler(viewId, params, message, severity) );
      }
      
      List<Element> childElements = element.elements("out");
      for (Element child: childElements)
      {
         Output output = new Output();
         output.setName( child.attributeValue("name") );
         output.setValue( Expressions.instance().createValueBinding( child.attributeValue("value") ) );
         String scopeName = child.attributeValue("scope");
         if (scopeName==null)
         {
            output.setScope(ScopeType.CONVERSATION);
         }
         else
         {
            output.setScope( ScopeType.valueOf( scopeName.toUpperCase() ) );
         }
         rule.getOutputs().add(output);
      }
      
   }
   
   public static Map<String, Severity> getFacesMessageValuesMap()
   {
      Map<String, Severity> result = new HashMap<String, Severity>();
      for (Map.Entry<String, Severity> me: (Set<Map.Entry<String, Severity>>) FacesMessage.VALUES_MAP.entrySet())
      {
         result.put( me.getKey().toUpperCase(), me.getValue() );
      }
      return result;
   }
   
   public String getLoginViewId()
   {
      return loginViewId;
   }
   public void setLoginViewId(String loginViewId)
   {
      this.loginViewId = loginViewId;
   }
   
}
