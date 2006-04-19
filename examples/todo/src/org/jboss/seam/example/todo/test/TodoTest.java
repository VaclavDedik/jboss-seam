//$Id$
package org.jboss.seam.example.todo.test;

import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.core.Actor;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Jbpm;
import org.jboss.seam.core.TaskInstanceList;
import org.jboss.seam.example.todo.Login;
import org.jboss.seam.example.todo.TodoList;
import org.jboss.seam.jsf.SeamExtendedManagedPersistencePhaseListener;
import org.jboss.seam.jsf.SeamPhaseListener;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class TodoTest extends SeamTest
{
   
   @Test
   public void testTodo() throws Exception
   {
      new Script() {

         @Override
         protected void updateModelValues() throws Exception
         {
            ( (Login) Component.getInstance(Login.class, true) ).setUser("gavin");
         }

         @Override
         protected void invokeApplication() throws Exception
         {
            assert "success".equals( ( (Login) Component.getInstance(Login.class, false) ).login() );
            assert Actor.instance().getId().equals("gavin");
         }

         @Override
         protected void renderResponse() throws Exception
         {
            assert ( (List) Component.getInstance(TaskInstanceList.class, true) ).size()==0;
         }
         
      }.run();
      
      new Script()
      {

         @Override
         protected void updateModelValues() throws Exception
         {
            ( (TodoList) Component.getInstance(TodoList.class, true) ).setDescription("Kick Roy out of my office");
         }

         @Override
         protected void invokeApplication() throws Exception
         {
            ( (TodoList) Component.getInstance(TodoList.class, false) ).createTodo();
         }
         
         @Override
         protected void renderResponse() throws Exception
         {
            assert ( (List) Component.getInstance(TaskInstanceList.class, true) ).size()==1;
         }
                  
         
      }.run();
   }
   
   @Override
   protected SeamPhaseListener createPhaseListener()
   {
      return new SeamExtendedManagedPersistencePhaseListener();
   }

   @Override
   public void initServletContext(Map initParams)
   {
      initParams.put(Init.COMPONENT_CLASSES, "org.jboss.seam.core.Jbpm org.jboss.seam.core.Microcontainer");
      initParams.put(Jbpm.PROCESS_DEFINITIONS, "todo.jpdl.xml");
      initParams.put(Init.JNDI_PATTERN, "#{ejbName}/local");
   }
   
}
