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
   
   private String nameVerification;
   
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
   
   public String getNameVerification()
   {
      return nameVerification;
   }

   public void setNameVerification(String nameVerification)
   {
      this.nameVerification = nameVerification;
   }
   
   public void check()
   {
      if (Strings.isEmpty(name))
      {
         statusMessages.addToControl("name", Severity.WARN, "Enter a name!");
      }
      if (Strings.isEmpty(nameVerification))
      {
         statusMessages.addToControl("nameVerification", Severity.WARN, "Enter a name verification!");
      }
      if (name != null && nameVerification != null && !name.equals(nameVerification))
      {
         statusMessages.addToControl("nameVerification", Severity.WARN, "Name and Name Verification not equal (should have been caught by equality validator!)");
      }
   }
   
}
