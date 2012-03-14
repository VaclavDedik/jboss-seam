//$Id: TodoTest.java 5299 2007-06-20 00:16:21Z gavin $
package org.jboss.seam.example.todo.test;

import java.io.File;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.bpm.TaskInstanceList;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class TodoTest extends JUnitSeamTest
{
   @Deployment(name = "TodoTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      EnterpriseArchive er = ShrinkWrap.create(ZipImporter.class, "seam-todo.ear").importFrom(new File("../todo-ear/target/seam-todo.ear")).as(EnterpriseArchive.class);
      WebArchive web = er.getAsType(WebArchive.class, "todo-web.war");
      web.addClasses(TodoTest.class);
      return er;
   }
   
   private long taskId;
   
   @Test
   public void testTodo() throws Exception
   {
      
      new FacesRequest() {

         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{login.user}", "gavin");
         }

         @Override
         protected void invokeApplication() throws Exception
         {
            assert invokeMethod("#{login.login}").equals("/todo.jsp");
            assert Actor.instance().getId().equals("gavin");
         }

         @Override
         protected void renderResponse() throws Exception
         {
            assert ( (List) getInstance(TaskInstanceList.class) ).size()==0;
         }
         
      }.run();
      
      new FacesRequest()
      {

         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{todoList.description}", "Kick Roy out of my office");
         }

         @Override
         protected void invokeApplication() throws Exception
         {
            invokeMethod("#{todoList.createTodo}");
         }
         
         @Override
         protected void renderResponse() throws Exception
         {
            List<TaskInstance> tasks = (List<TaskInstance>) getInstance(TaskInstanceList.class);
            assert tasks.size()==1;
            TaskInstance taskInstance = tasks.get(0);
            assert taskInstance.getDescription().equals("Kick Roy out of my office");
            taskId = taskInstance.getId();
         }
         
      }.run();

   
      new FacesRequest()
      {
   
         @Override
         protected void beforeRequest()
         {
            setParameter("taskId", Long.toString(taskId));
         }

         @Override
         protected void invokeApplication() throws Exception
         {
            invokeMethod("#{todoList.done}");
         }
         
         @Override
         protected void renderResponse() throws Exception
         {
            assert ( (List) getInstance(TaskInstanceList.class) ).size()==0;
         }
         
      }.run();
   }
   
}
