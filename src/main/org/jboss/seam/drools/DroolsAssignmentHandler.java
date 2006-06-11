package org.jboss.seam.drools;

import java.util.List;

import org.drools.WorkingMemory;
import org.hibernate.Session;
import org.jboss.seam.Component;
import org.jboss.seam.core.Actor;
import org.jboss.seam.jbpm.SeamVariableResolver;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;

/**
 * A jBPM AssignmentHandler that delegates to a Drools WorkingMemory
 * held in a Seam context variable. This implementation assumes that
 * the jBPM identity component is installed. Alternatively, if you
 * are not using the jBPM identity component for identity management,
 * you can subclass this implementation, and override the getUsers(),
 * getGroupByName() and getMemberShips() methods.
 * 
 * @author Jeff Delong
 * @author Gavin King
 *
 */
public class DroolsAssignmentHandler implements AssignmentHandler
{

   public String group;
   public String workingMemoryName;
   public List<String> objectNames;

   public void assign(Assignable assignable, ExecutionContext executionContext)
         throws Exception
   {
      WorkingMemory workingMemory = (WorkingMemory) Component.getInstance(workingMemoryName, true);

      // load the facts
      Session session = executionContext.getJbpmContext().getSession();
      assertObjects( getUsers(session), workingMemory );
      assertObjects( getGroupByName(session, group), workingMemory );
      assertObjects( getMemberships(session), workingMemory );

      for (String objectName: objectNames)
      {
         //TODO: support EL expressions here:
         Object object = new SeamVariableResolver().resolveVariable(objectName);
         // assert the object into the rules engine
         workingMemory.assertObject(object);
      }

      // assert the assignable so that it may be used to set results
      // TODO: any other useful objects?
      workingMemory.setGlobal( "assignable", assignable );
      workingMemory.setGlobal( "contextInstance", executionContext.getContextInstance() );
      workingMemory.assertObject( Actor.instance() );
      workingMemory.fireAllRules();
   }

   protected List getUsers(Session session)
   {
      return session.createQuery("from org.jbpm.identity.User").list();
   }

   protected List getGroupByName(Session session, String groupName)
   {
      return session.createQuery("from org.jbpm.identity.Group where name = :groupName")
            .setString("groupName", groupName).list();
   }

   protected List getMemberships(Session session)
   {
      return session.createQuery("from org.jbpm.identity.Membership").list();
   }

   private void assertObjects(List objectList, WorkingMemory workingMemory)
   {
      for (Object entity: objectList)
      {
         workingMemory.assertObject(entity);
      }
   }

}