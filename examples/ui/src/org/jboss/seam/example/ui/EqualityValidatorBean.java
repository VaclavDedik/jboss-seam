package org.jboss.seam.example.ui;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.util.Strings;

@Name("equalityValidatorBean")
@Scope(ScopeType.SESSION)
public class EqualityValidatorBean
{

   private String name;

   @In
   private StatusMessages statusMessages;

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public void check()
   {
      if (Strings.isEmpty(name))
      {
         statusMessages.addToControl("name", Severity.WARN, "Enter a name!");
      }
   }

}
