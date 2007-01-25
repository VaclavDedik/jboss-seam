package org.jboss.seam.example.seamspace;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.util.Date;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.annotations.security.Restrict;

@Stateful
@Name("blog")
public class BlogAction implements BlogLocal
{    
   @RequestParameter
   private String name;   
   
   @RequestParameter
   private Integer blogId;
   
   @In(create=true)
   private EntityManager entityManager;
   
   @In(required = false)
   private Member selectedMember;   
   
   @Out(required = false)
   private List memberBlogs;
   
   @In(required = false) @Out(required = false)
   private MemberBlog selectedBlog;
   
   @In(required = false) @Out(required = false, scope = CONVERSATION)
   private BlogComment comment;   
   
   @In(required = false)
   private Member authenticatedMember;
   
   /**
    * Returns the 5 latest blog entries for a member
    */
   public List getLatestBlogs()
   {
      return entityManager.createQuery(
           "from MemberBlog b where b.member = :member order by b.entryDate desc")
           .setParameter("member", selectedMember)
           .setMaxResults(5)
           .getResultList();
   }
   
   /**
    * Used to read all blog entries for a member
    */
   @Factory("memberBlogs")
   public void getMemberBlogs()
   {
      memberBlogs = entityManager.createQuery(
            "from MemberBlog b where b.member.memberName = :memberName order by b.entryDate desc")
            .setParameter("memberName", name)
            .getResultList();
   }
   
   /**
    * Used to read a single blog entry for a member
    */
   @Factory("selectedBlog") @Begin  
   public void getBlog()
   {
      try
      {
         selectedBlog = (MemberBlog) entityManager.createQuery(
           "from MemberBlog b where b.blogId = :blogId and b.member.memberName = :memberName")
           .setParameter("blogId", blogId)
           .setParameter("memberName", name)
           .getSingleResult();
      }
      catch (NoResultException ex) { }
   }
   
   @Factory("comment") @Restrict @Begin(join = true)
   public void createComment()
   {      
      comment = new BlogComment();
      comment.setCommentor(authenticatedMember);
      
      if (selectedBlog == null && name != null && blogId != null)
         getBlog();         
      
      comment.setBlog(selectedBlog);
   }
   
   public void previewComment()
   {
      // don't really need to do anything here...
   }
   
   public void saveComment()
   {      
      comment.setCommentDate(new Date());
      entityManager.persist(comment);
      
      // Reload the blog entry
      entityManager.refresh(selectedBlog);
   }     
   
   public void createEntry()
   {
      MemberBlog selectedBlog = new MemberBlog();              
   }
   
   @Remove @Destroy
   public void destroy() { }     
}
