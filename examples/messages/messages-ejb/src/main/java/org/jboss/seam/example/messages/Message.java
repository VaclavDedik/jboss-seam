//$Id: Message.java 902 2006-01-13 14:19:20Z theute $
package org.jboss.seam.example.messages;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Entity
@Name("message")
@Scope(EVENT)
public class Message implements Serializable
{

   private static final long serialVersionUID = -3304996108743093764L;
   
   private Long id;
   private String title;
   private String text;
   private boolean read;
   private Date datetime;
   
   @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
   public Long getId() {
      return id;
   }
   public void setId(Long id) {
      this.id = id;
   }
   
   @NotNull @Size(max=100)
   public String getTitle() {
      return title;
   }
   public void setTitle(String title) {
      this.title = title;
   }
   
   @NotNull @Lob
   public String getText() {
      return text;
   }
   public void setText(String text) {
      this.text = text;
   }
   
   @NotNull
   public boolean isRead() {
      return read;
   }
   public void setRead(boolean read) {
      this.read = read;
   }
   
   @NotNull 
   @Basic @Temporal(TemporalType.TIMESTAMP)
   public Date getDatetime() {
      return datetime;
   }
   public void setDatetime(Date datetime) {
      this.datetime = datetime;
   }
   
}
