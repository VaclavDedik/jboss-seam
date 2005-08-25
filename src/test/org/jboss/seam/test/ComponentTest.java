//$Id$
package org.jboss.seam.test;

import org.jboss.seam.Component;
import org.jboss.seam.ComponentType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.components.ConversationManager;
import org.jboss.seam.components.ManagedHibernateSession;
import org.jboss.seam.components.ManagedPersistenceContext;
import org.jboss.seam.components.Settings;
import org.testng.annotations.Test;

public class ComponentTest
{
   @Test
   public void testComponent()
   {
      Component c = new Component(Bar.class);
      assert c.getName().equals("bar");
      assert c.getBeanClass()==Bar.class;
      assert c.getType()==ComponentType.JAVA_BEAN;
      assert c.getScope()==ScopeType.CONVERSATION;
      assert c.hasDestroyMethod();
      assert c.hasCreateMethod();
      assert c.getCreateMethod().getName().equals("create");
      assert c.getDestroyMethod().getName().equals("destroy");
      assert c.getInFields().size()==2;
      assert c.getInMethods().size()==0;
      assert c.isConversational();
      assert c.getNoConversationOutcome().equals("error");
      assert c.getUnwrapMethod()==null;
      assert c.getOutFields().size()==1;
      assert c.getOutMethods().size()==0;
      assert c.getRemoveMethods().size()==0;
      assert c.getValidateMethods().size()==0;
      assert c.isInstance( new Bar() );

      c = new Component(Foo.class);
      assert c.getName().equals("foo");
      assert c.getBeanClass()==Foo.class;
      assert c.getType()==ComponentType.JAVA_BEAN;
      assert c.getScope()==ScopeType.SESSION;
      assert !c.hasDestroyMethod();
      assert !c.hasCreateMethod();
      assert c.getCreateMethod()==null;
      assert c.getDestroyMethod()==null;
      assert c.getInFields().size()==0;
      assert c.getInMethods().size()==0;
      assert !c.isConversational();
      assert c.getUnwrapMethod()==null;
      assert c.getOutFields().size()==0;
      assert c.getOutMethods().size()==0;
      assert c.getRemoveMethods().size()==1;
      assert c.getValidateMethods().size()==1;
      assert c.isInstance( new Foo() );
      
      //TODO: session beans
   }
   
   public void testBuiltInComponents()
   {
      Component c = new Component(ConversationManager.class);
      assert c.getName().equals("org.jboss.seam.conversationManager");
      assert c.getBeanClass()==ConversationManager.class;
      assert c.getType()==ComponentType.JAVA_BEAN;
      assert c.getScope()==ScopeType.EVENT;
      assert c.hasDestroyMethod();
      assert !c.hasCreateMethod();
      assert c.getCreateMethod()==null;
      assert c.getDestroyMethod().getName().equals("destroy");
      assert c.getInFields().size()==0;
      assert c.getInMethods().size()==0;
      assert !c.isConversational();
      assert c.getUnwrapMethod()==null;
      assert c.getOutFields().size()==0;
      assert c.getOutMethods().size()==0;
      assert c.getRemoveMethods().size()==0;
      assert c.getValidateMethods().size()==0;

      c = new Component(Settings.class);
      assert c.getName().equals("org.jboss.seam.settings");
      assert c.getBeanClass()==Settings.class;
      assert c.getType()==ComponentType.JAVA_BEAN;
      assert c.getScope()==ScopeType.APPLICATION;
      assert !c.hasDestroyMethod();
      assert !c.hasCreateMethod();
      assert c.getCreateMethod()==null;
      assert c.getDestroyMethod()==null;
      assert c.getInFields().size()==0;
      assert c.getInMethods().size()==0;
      assert !c.isConversational();
      assert c.getUnwrapMethod()==null;
      assert c.getOutFields().size()==0;
      assert c.getOutMethods().size()==0;
      assert c.getRemoveMethods().size()==0;
      assert c.getValidateMethods().size()==0;

      c = new Component(ManagedPersistenceContext.class, "pc");
      assert c.getName().equals("pc");
      assert c.getBeanClass()==ManagedPersistenceContext.class;
      assert c.getType()==ComponentType.JAVA_BEAN;
      assert c.getScope()==ScopeType.CONVERSATION;
      assert c.hasDestroyMethod();
      assert c.hasCreateMethod();
      assert c.getCreateMethod().getName().equals("create");
      assert c.getDestroyMethod().getName().equals("destroy");
      assert c.getInFields().size()==0;
      assert c.getInMethods().size()==0;
      assert !c.isConversational();
      assert c.getUnwrapMethod().getName().equals("getEntityManager");
      assert c.getOutFields().size()==0;
      assert c.getOutMethods().size()==0;
      assert c.getRemoveMethods().size()==0;
      assert c.getValidateMethods().size()==0;

      c = new Component(ManagedHibernateSession.class, "pc");
      assert c.getName().equals("pc");
      assert c.getBeanClass()==ManagedHibernateSession.class;
      assert c.getType()==ComponentType.JAVA_BEAN;
      assert c.getScope()==ScopeType.CONVERSATION;
      assert c.hasDestroyMethod();
      assert c.hasCreateMethod();
      assert c.getCreateMethod().getName().equals("create");
      assert c.getDestroyMethod().getName().equals("destroy");
      assert c.getInFields().size()==0;
      assert c.getInMethods().size()==0;
      assert !c.isConversational();
      assert c.getUnwrapMethod().getName().equals("getSession");
      assert c.getOutFields().size()==0;
      assert c.getOutMethods().size()==0;
      assert c.getRemoveMethods().size()==0;
      assert c.getValidateMethods().size()==0;
   }
}
