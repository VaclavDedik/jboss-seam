package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.jboss.seam.annotations.Name;

@Entity
@Name("memberBlog")
public class MemberBlog implements Serializable
{
   private static final long serialVersionUID = 7824113911888715595L;
   
   private static SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM d, yyyy - hh:mm a");
   
   private Integer blogId;
   private Member member;
   private Date entryDate;
   private String title;
   private String text;
   
   private List<BlogComment> comments;
   
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
   
   @Transient
   public String getFormattedEntryDate()
   {
      return df.format(entryDate);
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
   
   @OneToMany
   public List<BlogComment> getComments()
   {
      return comments;
   }
   
   public void setComments(List<BlogComment> comments)
   {
      this.comments = comments;
   }
   
   @Transient
   public int getCommentCount()
   {
      return comments.size();
   }
}
