//$Id$
package org.jboss.seam.example.bpm.test;

import java.util.List;
import java.util.Map;

import javax.faces.model.DataModel;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Actor;
import org.jboss.seam.core.Ejb;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Jbpm;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.TaskInstanceList;
import org.jboss.seam.example.bpm.Document;
import org.jboss.seam.example.bpm.DocumentCreation;
import org.jboss.seam.example.bpm.DocumentCreationHandler;
import org.jboss.seam.example.bpm.DocumentTask;
import org.jboss.seam.example.bpm.DocumentTaskHandler;
import org.jboss.seam.example.bpm.MyDocuments;
import org.jboss.seam.example.bpm.MyDocumentsHandler;
import org.jboss.seam.example.bpm.Status;
import org.jboss.seam.example.bpm.User;
import org.jboss.seam.mock.SeamTest;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.testng.annotations.Test;

public class DocumentLifecycleTest extends SeamTest
{
   
   @Test
   public void testWorkflow() throws Exception
   {
            
      new Script() {
         
         @Override
         protected void invokeApplication() throws Exception {
            //shortcut the login cycle
            Contexts.getSessionContext().set("loggedIn", true);
            User user = new User();
            user.setId(2l);
            user.setUsername("user");
            Contexts.getSessionContext().set("user", user);
            Actor.instance().setId("user");
         }

         @Override
         protected void renderResponse() throws Exception {
            Document doc = (Document) Component.getInstance(Document.class, true);
            DocumentCreation creator = (DocumentCreation) Component.getInstance(DocumentCreationHandler.class, true);
            assert creator.isNew();
            assert creator.isEditable();
            assert doc.getContent()==null;
            assert doc.getTitle()==null;
            assert !Manager.instance().isLongRunningConversation();
         }
         
      }.run();

      new Script() {

         @Override
         protected void updateModelValues() throws Exception {
            Document doc = (Document) Component.getInstance(Document.class, true);
            doc.setContent("The content of the document");
            doc.setTitle("The Document Title");
            
         }

         @Override
         protected void invokeApplication() throws Exception {
            DocumentCreation doc = (DocumentCreation) Component.getInstance(DocumentCreationHandler.class, true);
            assert doc.create().equals("detail");
         }

         @Override
         protected void renderResponse() throws Exception {            
            Document doc = (Document) Component.getInstance(Document.class, true);
            assert doc.getContent().equals("The content of the document");
            assert doc.getTitle().equals("The Document Title");
            assert doc.getStatus()==Status.PENDING;
            assert doc.getSubmittedTimestamp()!=null;
            DocumentCreation creator = (DocumentCreation) Component.getInstance(DocumentCreationHandler.class, true);
            assert !creator.isNew();
            assert creator.isEditable();
            assert Manager.instance().isLongRunningConversation();
            assert Contexts.getBusinessProcessContext().get("documentId").equals( doc.getId() );
            assert Contexts.getBusinessProcessContext().get("description").equals("The Document Title");
            assert Contexts.getBusinessProcessContext().get("submitter").equals("user");
         }
         
      }.run();
      
      String id = new Script() {
         
         @Override
         protected void renderResponse() throws Exception {
            DataModel documents = (DataModel) Component.getInstance("documents", true);
            assert documents.getRowCount()==1;
            Document doc = (Document) documents.getRowData();
            assert doc.getContent().equals("The content of the document");
            assert doc.getTitle().equals("The Document Title");
            assert doc.getStatus()==Status.PENDING;
            assert Manager.instance().isLongRunningConversation();
         }
      
      }.run();
      
      new Script(id) {

         @Override
         protected void invokeApplication() throws Exception {
            MyDocuments mydoc = (MyDocuments) Component.getInstance(MyDocumentsHandler.class, true);
            assert mydoc.select().equals("detail");
         }

         @Override
         protected void renderResponse() throws Exception {
            Document doc = (Document) Component.getInstance(Document.class, true);
            assert doc.getContent().equals("The content of the document");
            assert doc.getTitle().equals("The Document Title");
            assert doc.getStatus()==Status.PENDING;
            DocumentCreation creator = (DocumentCreation) Component.getInstance(DocumentCreationHandler.class, true);
            assert !creator.isNew();
            assert creator.isEditable();
            assert Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
       
      final Long[] taskId = new Long[1];
      
      new Script() {
         
         @Override
         protected void invokeApplication() throws Exception {
            //shortcut the login cycle
            Contexts.getSessionContext().set("loggedIn", true);
            User user = new User();
            user.setId(1l);
            user.setUsername("admin");
            Contexts.getSessionContext().set("user", user);
            Actor.instance().setId("admin");
         }
         
         @Override
         protected void renderResponse() throws Exception {
            List<TaskInstance> tasks = (List) Component.getInstance(TaskInstanceList.class, true);
            assert tasks.size()==1;
            TaskInstance instance = tasks.get(0);
            taskId[0] = instance.getId();
            assert !Manager.instance().isLongRunningConversation();
         }
         
      }.run();
     
      id = new Script() {
         @Override
         protected void setParameters() {
            getRequestParameterMap().put("taskId", taskId[0].toString());
         }

         @Override
         protected void invokeApplication() throws Exception {
            DocumentTask docTask = (DocumentTask) Component.getInstance(DocumentTaskHandler.class, true);
            assert docTask.details().equals("review");
         }

         @Override
         protected void renderResponse() throws Exception {
            Document doc = (Document) Component.getInstance(Document.class, true);
            assert doc.getContent().equals("The content of the document");
            assert doc.getTitle().equals("The Document Title");
            assert doc.getStatus()==Status.PENDING;
            assert doc.getSubmittedTimestamp()!=null;
            assert Manager.instance().isLongRunningConversation();
            assert Contexts.getBusinessProcessContext().get("documentId").equals( doc.getId() );
            assert Contexts.getBusinessProcessContext().get("description").equals("The Document Title");
            assert Contexts.getBusinessProcessContext().get("submitter").equals("user");
         }
         
       }.run();

       new Script(id) {
          
          @Override
          protected void invokeApplication() throws Exception {
             DocumentTask docTask = (DocumentTask) Component.getInstance(DocumentTaskHandler.class, true);
             assert docTask.approve().equals("approved");
          }

          @Override
          protected void renderResponse() throws Exception {
             Document doc = (Document) Component.getInstance(Document.class, true);
             assert doc.getContent().equals("The content of the document");
             assert doc.getTitle().equals("The Document Title");
             assert doc.getStatus()==Status.APPROVED;
             assert !Manager.instance().isLongRunningConversation();
          }
          
        }.run();

        new Script() {
           
           @Override
           protected void renderResponse() throws Exception {
              List<TaskInstance> tasks = (List) Component.getInstance(TaskInstanceList.class, true);
              assert tasks.size()==0;
           }
           
        }.run();
   
   }
      
   @Override
   public void initServletContext(Map initParams)
   {
      initParams.put( Init.COMPONENT_CLASSES, Ejb.class.getName() + ' ' + Jbpm.class.getName());
      initParams.put( Init.JBPM_SESSION_FACTORY_NAME, "jbpmSessionFactory" );
      initParams.put( Jbpm.PROCESS_DEFINITIONS, "jbpm-DocumentSubmission.xml");
   }
   
}
