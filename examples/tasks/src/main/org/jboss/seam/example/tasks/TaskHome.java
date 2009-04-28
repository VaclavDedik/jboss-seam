package org.jboss.seam.example.tasks;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.tasks.entity.Task;
import org.jboss.seam.framework.EntityHome;

@Name("taskHome")
@AutoCreate
public class TaskHome extends EntityHome<Task>
{
   public Task findTask(Long context, String task) {
      setInstance((Task) getEntityManager().createNamedQuery("taskByNameAndContext").setParameter("task", task).setParameter("context", context).getSingleResult());
      return getInstance();
   }
   
}
