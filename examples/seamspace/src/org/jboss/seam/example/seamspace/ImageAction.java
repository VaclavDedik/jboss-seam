package org.jboss.seam.example.seamspace;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.SeamSecurityManager;

@Stateless
@Name("imageAction")
public class ImageAction implements ImageLocal
{
   @In(create = true) EntityManager entityManager;
   
   public MemberImage getImage(int imageId)
   {
      MemberImage img = entityManager.find(MemberImage.class, imageId);

      if (img != null && SeamSecurityManager.hasPermission("MemberImage", "view", img))      
         return img;
      else
         return null;
   }
}
