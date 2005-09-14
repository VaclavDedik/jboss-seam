package org.jboss.seam.example.bpm;

import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.In;
import org.jbpm.db.JbpmSession;
import org.jbpm.taskmgmt.exe.TaskInstance;

import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 */
public class DocumentTaskListHandler implements DocumentTaskList, Serializable
{
   @PersistenceContext
   private EntityManager entityManager;

//   @In
//   private User user;

   @In( value="org.jboss.seam.core.managedJbpmSession" )
   private JbpmSession jbpmSession;

   private List<DocumentTask> documentTasks;

   public String find() throws Exception
   {
      List<TaskInstance> tasks = jbpmSession.getTaskMgmtSession().findTaskInstances( "admin" );
      documentTasks = new ArrayList<DocumentTask>( tasks.size() );
      for ( TaskInstance task : tasks )
      {
         Long documentId = ( Long ) task.getTaskMgmtInstance()
               .getProcessInstance()
               .getContextInstance()
               .getVariable( "documentId" );
         final Document document = entityManager.find( Document.class, documentId );
         documentTasks.add( generateDocumentTask( document, task ) );
      }
      return "main";
   }

   public String selectTask()
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public String nextTask()
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public String previousTask()
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   private DocumentTask generateDocumentTask(Document document, TaskInstance task)
   {
      return new DocumentTask(
            task.getId(),
            task.getName(),
            document.getId(),
            document.getTitle(),
            document.getStatus()
      );
   }

   public static class DocumentTask implements Serializable
   {
      private final long taskId;
      private final String taskName;
      private final long documentId;
      private final String documentTitle;
      private final Status documentStatus;

      public DocumentTask(
            long taskId,
            String taskName,
            long documentId,
            String documentTitle,
            Status documentStatus)
      {
         this.taskId = taskId;
         this.taskName = taskName;
         this.documentId = documentId;
         this.documentTitle = documentTitle;
         this.documentStatus = documentStatus;
      }

      public long getTaskId()
      {
         return taskId;
      }

      public String getTaskName()
      {
         return taskName;
      }

      public long getDocumentId()
      {
         return documentId;
      }

      public String getDocumentTitle()
      {
         return documentTitle;
      }

      public Status getDocumentStatus()
      {
         return documentStatus;
      }
   }
}
