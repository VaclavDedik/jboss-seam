package org.jboss.seam.example.tasks.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.NotNull;

@Entity
@XmlRootElement
@NamedQuery(name="taskByNameAndContext", query="select task from Task task where task.name like :task and task.context.id = :context")
public class Task
{
   private Long id;
   private String name;
   private boolean resolved;
   private Date created;
   private Date updated;
   private Context context;

   @Id
   @GeneratedValue
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

   @NotNull
   public boolean isResolved()
   {
      return resolved;
   }

   public void setResolved(boolean resolved)
   {
      this.resolved = resolved;
   }

   @Temporal(TemporalType.TIMESTAMP)
   @XmlElement(name="created")
   @NotNull
   public Date getCreated()
   {
      return created;
   }

   public void setCreated(Date created)
   {
      this.created = created;
   }

   @Temporal(TemporalType.TIMESTAMP)
   public Date getUpdated()
   {
      return updated;
   }

   public void setUpdated(Date updated)
   {
      this.updated = updated;
   }

   @ManyToOne
   @XmlTransient
   @NotNull
   public Context getContext()
   {
      return context;
   }
   

   public void setContext(Context context)
   {
      this.context = context;
   }
   
   @Transient
   @XmlElement(name="context")
   public String getContextName() {
      return context.getName();
   }
   
   @Transient
   public User getOwner() {
      return context.getOwner();
   }
   
}
