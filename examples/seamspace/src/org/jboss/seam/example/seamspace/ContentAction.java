package org.jboss.seam.example.seamspace;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Identity;

@Stateless
@Name("contentAction")
public class ContentAction implements ContentLocal
{
   @In EntityManager entityManager;   
   
   public MemberImage getImage(int imageId)
   {
      MemberImage img = entityManager.find(MemberImage.class, imageId);
      
      if (img == null || !Identity.instance().hasPermission("memberImage", "view", img))
         return null;
      else
         return img;
   }
}
