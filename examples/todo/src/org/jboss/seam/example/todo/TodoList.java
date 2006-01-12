package org.jboss.seam.example.todo;

import org.jboss.seam.annotations.CreateProcess;
import org.jboss.seam.annotations.EndTask;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.StartTask;
import org.jboss.seam.contexts.Contexts;

@Name("todoList")
public class TodoList {
   
   private String description;
   
   public String getDescription()
   {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }
   
   @CreateProcess(definition="todo")
   public void createTodo() {}
   
   @StartTask @EndTask
   public void done() {}

}
