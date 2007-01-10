package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.jboss.seam.annotations.Name;

@Entity
@Name("memberBlog")
public class MemberBlog implements Serializable
{
   private static final long serialVersionUID = 7824113911888715595L;
   
   private Integer blogId;
   private Member member;
   private Date entryDate;
   private String title;
   private String text;
   
   @Id
   public Integer getBlogId()
   {
      return blogId;
   }
   
   public void setBlogId(Integer blogId)
   {
      this.blogId = blogId;
   }

   public Date getEntryDate()
   {
      return entryDate;
   }

   public void setEntryDate(Date entryDate)
   {
      this.entryDate = entryDate;
   }

   @ManyToOne
   @JoinColumn(name = "MEMBER_ID")   
   public Member getMember()
   {
      return member;
   }

   public void setMember(Member member)
   {
      this.member = member;
   }

   public String getText()
   {
      return text;
   }

   public void setText(String text)
   {
      this.text = text;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }
}
