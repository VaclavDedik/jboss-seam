package org.jboss.seam.example.ui;

import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

@Name("selectDate")
@Scope(ScopeType.CONVERSATION)
public class SelectDate
{

   private Date date = new Date();
   
   public Date getDate()
   {
      return date;
   }
   
   public void setDate(Date date)
   {
      this.date = date;
   }
   
   public void echo()
   {
      FacesMessages.instance().add("Date: " + getDate());
   }
   
}
