package org.jboss.seam.example.tasks.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.NotNull;

@Entity
@XmlRootElement
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "NAME", "OWNER_USERNAME" }))
@NamedQuery(name = "contextByNameAndUser", query = "select context from Context context where context.owner.username like :username and context.name like :context")
public class Context
{
   private Long id;
   private String name;
   private List<Task> tasks;
   private User owner;

   public Context()
   {
   }

   public Context(Long id, String name, List<Task> tasks, User owner)
   {
      this.id = id;
      this.name = name;
      this.tasks = tasks;
      this.owner = owner;
   }

   @Id
   @GeneratedValue
   @XmlTransient
   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   @NotNull
   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   @OneToMany(mappedBy = "context", cascade = CascadeType.REMOVE)
   @XmlTransient
   public List<Task> getTasks()
   {
      return tasks;
   }

   public void setTasks(List<Task> tasks)
   {
      this.tasks = tasks;
   }

   @ManyToOne
   @XmlTransient
   public User getOwner()
   {
      return owner;
   }

   public void setOwner(User owner)
   {
      this.owner = owner;
   }

}
