package org.jboss.seam.mail;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("org.jboss.seam.mail.meldwareUser")
@Scope(APPLICATION)
@Intercept(NEVER)
@Install(precedence=BUILT_IN, dependencies="org.jboss.seam.mail.meldware", value=false)
public class MeldwareUser
{
   
   private String username;
   private String password;
   
   private boolean administrator;
   
   private List<String> aliases;

   public boolean isAdministrator()
   {
      return administrator;
   }

   public void setAdministrator(boolean admin)
   {
      this.administrator = admin;
   }

   public List<String> getAliases()
   {
      return aliases;
   }

   public void setAliases(List<String> aliases)
   {
      this.aliases = aliases;
   }

   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   public String getUsername()
   {
      return username;
   }

   public void setUsername(String username)
   {
      this.username = username;
   }
   
   public List<String> getRoles()
   {
      return getRoles(this);
   }
   
   private static List<String> getRoles(MeldwareUser user)
   {
      List<String> roles = new ArrayList<String>();
      roles.add("calendaruser");
      if (user.isAdministrator())
      {
         roles.add("adminuser");
      }
      return roles;
   }

}
