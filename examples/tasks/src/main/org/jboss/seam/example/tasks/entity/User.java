package org.jboss.seam.example.tasks.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.NotNull;

@Entity
@XmlRootElement
public class User
{
   private String username;
   private String password;
   private List<Context> contexts;
   private boolean admin;

   @Id
   public String getUsername()
   {
      return username;
   }

   public void setUsername(String username)
   {
      this.username = username;
   }

   @NotNull
   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   @OneToMany(mappedBy = "owner")
   @XmlTransient
   public List<Context> getContexts()
   {
      return contexts;
   }

   public void setContexts(List<Context> contexts)
   {
      this.contexts = contexts;
   }

   public boolean isAdmin()
   {
      return admin;
   }

   public void setAdmin(boolean admin)
   {
      this.admin = admin;
   }
}
