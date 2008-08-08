package org.jboss.seam.excel;

import java.util.LinkedList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("excelTest")
@Scope(ScopeType.SESSION)
public class ExcelTest
{

   private List<Person> people = new LinkedList<Person>();
   
   public List<Person> getPeople()
   {
      List<Person> ret = new LinkedList<Person>();
      for (int i = 0; i < 10; i++)
      {
         ret.add(new Person(i, "Janne" , "Andersson " + i));
      }
      return ret;

   }
   
   public List<Person> getResult()
   {
      return people;
   }
   
   public void search() {
      this.people = getPeople();
   }
   
   public void clear() {
      this.people = new LinkedList<Person>();
   }

   public class Person
   {
      int age;
      String name;
      String lastName;

      public Person(int age, String name, String lastName)
      {
         super();
         this.age = age;
         this.name = name;
         this.lastName = lastName;
      }

      public int getAge()
      {
         return age;
      }

      public void setAge(int age)
      {
         this.age = age;
      }

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }

      public String getLastName()
      {
         return lastName;
      }

      public void setLastName(String lastName)
      {
         this.lastName = lastName;
      }
   }

}
