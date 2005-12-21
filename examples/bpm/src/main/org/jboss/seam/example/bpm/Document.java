package org.jboss.seam.example.bpm;

import java.util.Date;

import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Domain representation of a user-submitted document.
 *
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 */
@Entity( access= AccessType.FIELD )
@Table( name="T_DOCUMENT" )
@Name( "document" )
@Scope( ScopeType.CONVERSATION )
@NamedQuery(name="myDocuments", 
      queryString="select d from Document as d where d.submitter = :user")
public class Document
{
   @Id( generate = GeneratorType.AUTO )
   private Long id;
   @Version
   private int version;
   
   @NotNull
   private Status status = Status.PENDING;
   
   @NotNull 
   @Length(min=3, max=200, message="Title of length less than 200 is required")
   private String title;
   
   @NotNull 
   @Length(min=3, message="Content is required") 
   @Lob
   private String content;
   
   private Date submittedTimestamp;
   @ManyToOne
   private User submitter;
   
   private Date approvalTimestamp;
   @ManyToOne
   private User approver;

   public Document()
   {
   }

   public Long getId()
   {
      return id;
   }

   public int getVersion()
   {
      return version;
   }

   public Status getStatus()
   {
      return status;
   }

   public void setStatus(Status status)
   {
      this.status = status;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public String getContent()
   {
      return content;
   }

   public void setContent(String content)
   {
      this.content = content;
   }

   public Date getSubmittedTimestamp()
   {
      return submittedTimestamp;
   }

   public void setSubmittedTimestamp(Date submittedTimestamp)
   {
      this.submittedTimestamp = submittedTimestamp;
   }

   public User getSubmitter()
   {
      return submitter;
   }

   public void setSubmitter(User submitter)
   {
      this.submitter = submitter;
   }

   public Date getApprovalTimestamp()
   {
      return approvalTimestamp;
   }

   public void setApprovalTimestamp(Date approvalTimestamp)
   {
      this.approvalTimestamp = approvalTimestamp;
   }

   public User getApprover()
   {
      return approver;
   }

   public void setApprover(User approver)
   {
      this.approver = approver;
   }

   public void approve(User approver)
   {
      this.approver = approver;
      this.approvalTimestamp = new Date();
      this.status = Status.APPROVED;
   }

   public void reject(User approver)
   {
      this.approver = approver;
      this.approvalTimestamp = new Date();
      this.status = Status.REJECTED;
   }
}
