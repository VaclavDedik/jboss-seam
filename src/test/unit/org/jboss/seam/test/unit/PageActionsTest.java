package org.jboss.seam.test.unit;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.FacesLifecycle;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.ResourceLoader;
import org.jboss.seam.faces.FacesManager;
import org.jboss.seam.mock.MockApplication;
import org.jboss.seam.mock.MockExternalContext;
import org.jboss.seam.mock.MockFacesContext;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.util.Conversions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.faces.context.FacesContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The purpose of this test is to verify the way that page actions are handled. Once
 * a page action triggers a navigation event, subsequent page actions in the chain
 * should be short circuited.
 */
public class PageActionsTest
{
   @BeforeMethod
   public void setup()
   {
      // create main application map
      Lifecycle.beginApplication(new HashMap<String, Object>());

      // start all the contexts
      Lifecycle.beginCall();

      // establish the FacesContext
      new MockFacesContext(new MockExternalContext(), new MockApplication()).setCurrent().createViewRoot();
      FacesLifecycle.resumePage();

      // install key components
      installComponents(Contexts.getApplicationContext());

      // initialize pages from WEB-INF/pages.xml
      Pages.instance();

      // mark the application as started
      Lifecycle.mockApplication();
   }

   @AfterMethod
   public void tearDown()
   {
      Lifecycle.endApplication();
      Lifecycle.unmockApplication();
   }

   /**
    * This test verifies that a non-null outcome will short-circuit the page
    * actions. It tests two difference variations. The first variation includes
    * both actions as nested elements of the page node. The second variation has
    * the first action in the action attribute of the page node and the second
    * action as a nested element. Aside from the placement of the actions, the
    * two parts of the test are equivalent.
    */
   @Test(enabled = true)
   public void testShortCircuitOnNonNullOutcome()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      TestActions testActions = TestActions.instance();

      facesContext.getViewRoot().setViewId("/action-test01a.xhtml");
      Pages.instance().preRender(facesContext);
      assertViewId(facesContext, "/pageA.xhtml");
      assertActionCalls(testActions, new String[] { "nonNullActionA" });

      testActions = TestActions.instance();

      facesContext.getViewRoot().setViewId("/action-test01b.xhtml");
      Pages.instance().preRender(facesContext);
      assertViewId(facesContext, "/pageA.xhtml");
      assertActionCalls(testActions, new String[] { "nonNullActionA" });
   }

   /**
    * This test verifies that because the first action does not result in a
    * navigation, the second action is executed. However, the third action is
    * not called because of the navigation on the second action.
    */
   @Test(enabled = true)
   public void testShortCircuitInMiddle()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      TestActions testActions = TestActions.instance();

      facesContext.getViewRoot().setViewId("/action-test02.xhtml");
      Pages.instance().preRender(facesContext);
      assertViewId(facesContext, "/pageB.xhtml");
      assertActionCalls(testActions, new String[] { "nonNullActionA", "nonNullActionB" });
   }

   @Test(enabled = true)
   public void testShortCircuitOnNullOutcome()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      TestActions testActions = TestActions.instance();

      facesContext.getViewRoot().setViewId("/action-test03.xhtml");
      Pages.instance().preRender(facesContext);
      assertViewId(facesContext, "/pageA.xhtml");
      assertActionCalls(testActions, new String[] { "nullActionA" });
   }

   /**
    * Verify that the first non-null outcome, even if it is to the same view id,
    * will short circuit the action calls.
    */
   @Test(enabled = true)
   public void testShortCircuitOnFirstNonNullOutcome()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      TestActions testActions = TestActions.instance();

      facesContext.getViewRoot().setViewId("/action-test04.xhtml");
      Pages.instance().preRender(facesContext);
      assertViewId(facesContext, "/action-test04.xhtml");
      assertActionCalls(testActions, new String[] { "nullActionA", "nonNullActionB" });
   }

   /**
    * Same as testShortCircuitOnNonNullOutcome except that the navigation rules
    * are redirects rather than renders.
    */
   @Test(enabled = true)
   public void testShortCircuitOnNonNullOutcomeWithRedirect()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      TestActions testActions = TestActions.instance();

      facesContext.getViewRoot().setViewId("/action-test05.xhtml");
      Pages.instance().preRender(facesContext);
      assertViewId(facesContext, "/action-test05.xhtml");
      assertActionCalls(testActions, new String[] { "nonNullActionA" });
      assert Contexts.getEventContext().get("lastRedirectViewId").equals("/pageA.xhtml") : 
         "Expecting a redirect to /pageA.xhtml but redirected to " + Contexts.getEventContext().get("lastRedirectViewId");
      assert facesContext.getResponseComplete() == true : "The response should have been marked as complete";
   }
   
   /**
    * This test is here (and disabled) to demonstrate the old behavior. All page
    * actions would be executed regardless and navigations could cross page
    * declaration boundaries since the view id is changing mid-run (hence
    * resulting in different navigation rule matches)
    */
   @Test(enabled = false)
   public void oldBehaviorTest()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      TestActions testActions = TestActions.instance();

      facesContext.getViewRoot().setViewId("/action-test99a.xhtml");
      Pages.instance().preRender(facesContext);
      assertViewId(facesContext, "/pageB.xhtml");
      assertActionCalls(testActions, new String[] { "nonNullActionA", "nonNullActionB" });
   }

   private void assertViewId(FacesContext facesContext, String expectedViewId)
   {
      String actualViewId = facesContext.getViewRoot().getViewId();
      assert expectedViewId.equals(actualViewId) :
         "Expected viewId to be " + expectedViewId + ", but got " + actualViewId;
   }

   private void assertActionCalls(TestActions testActions, String[] methodNames)
   {
      List<String> actionsCalled = testActions.getActionsCalled();
      assert actionsCalled.size() == methodNames.length :
         "Expected " + methodNames.length + " action(s) to be called, but executed " + actionsCalled.size() + " action(s) instead";
      String expectedMethodCalls = "";
      for (int i = 0, len = methodNames.length; i < len; i++)
      {
         if (i > 0)
         {
            expectedMethodCalls += ", ";
         }
         expectedMethodCalls += methodNames[i];
      }
      String actualMethodCalls = "";
      for (int i = 0, len = actionsCalled.size(); i < len; i++)
      {
         if (i > 0)
         {
            actualMethodCalls += ", ";
         }
         actualMethodCalls += actionsCalled.get(i);
      }

      assert expectedMethodCalls.equals(actualMethodCalls) :
         "Expected actions to be called: " + expectedMethodCalls + "; actions actually called: " + actualMethodCalls;

      Contexts.getEventContext().remove(Component.getComponentName(TestActions.class));
   }

   private void installComponents(Context appContext)
   {
      Init init = new Init();
      init.setTransactionManagementEnabled(false);
      appContext.set(Seam.getComponentName(Init.class), init);
      Map<String, Conversions.PropertyValue> properties = new HashMap<String, Conversions.PropertyValue>();
      appContext.set(Component.PROPERTIES, properties);
      properties.put(Seam.getComponentName(Pages.class) + ".resources", new Conversions.FlatPropertyValue("/META-INF/pagesForPageActionsTest.xml"));

      installComponent(appContext, NoRedirectFacesManager.class);
      installComponent(appContext, ResourceLoader.class);
      installComponent(appContext, Expressions.class);
      installComponent(appContext, Pages.class);

      installComponent(appContext, TestActions.class);
   }

   private void installComponent(Context appContext, Class clazz)
   {
      appContext.set(Seam.getComponentName(clazz) + ".component", new Component(clazz));
   }

   @Scope(ScopeType.EVENT)
   @Name("org.jboss.seam.core.manager")
   @BypassInterceptors
   public static class NoRedirectFacesManager extends FacesManager {

      @Override
      public void redirect(String viewId, Map<String, Object> parameters, boolean includeConversationId)
      {
         Contexts.getEventContext().set("lastRedirectViewId", viewId);
         // a lot of shit happens we don't need; the important part is that the
         // viewId is not changed on FacesContext, but the response is marked complete
         FacesContext.getCurrentInstance().responseComplete();
      }
      
   }

}
