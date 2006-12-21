package org.jboss.seam.example.seamspace;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.jboss.seam.annotations.Name;

@Entity
@Name("memberImage")
public class MemberImage implements Serializable
{
   private static final long serialVersionUID = -8088455267213832920L;
   
   private Integer imageId;
   private Member member;
   private byte[] data;
   private String contentType;
   
   @Id
   public Integer getImageId()
   {
      return imageId;
   }
   
   public void setImageId(Integer imageId)
   {
      this.imageId = imageId;
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

   public String getContentType()
   {
      return contentType;
   }

   public void setContentType(String contentType)
   {
      this.contentType = contentType;
   }

   public byte[] getData()
   {
      return data;
   }

   public void setData(byte[] data)
   {
      this.data = data;
   }
}
