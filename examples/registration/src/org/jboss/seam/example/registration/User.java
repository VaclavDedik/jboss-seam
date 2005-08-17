package org.jboss.seam.example.registration;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.Name;

@Entity
@Name("user")
public class User implements Serializable
{

   private String username;
   private String name;
   private String password;
   private int age;
   private int version = -1;

   public User() {}

   @Id @Length(min=6, max=15)
   public String getUsername()
   {
      return username;
   }

   public void setUsername(String username)
   {
      this.username = username;
   }
   
   @NotNull @Length(min=6, max=15)
   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }
   
   @NotNull
   public int getAge()
   {
      return age;
   }

   public void setAge(int age)
   {
      this.age = age;
   }
   
   @NotNull @Length(max=100)
   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }
   
   @Version
   public int getVersion()
   {
      return version;
   }

   public void setVersion(int version)
   {
      this.version = version;
   }

}
