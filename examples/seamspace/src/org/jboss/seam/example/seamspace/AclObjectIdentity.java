package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Identifies a persistent object for the purpose of defining an Acl for it
 * 
 * @author Shane Bryzak
 */
@Entity
public class AclObjectIdentity implements Serializable
{
   private static final long serialVersionUID = 1L;

   private Integer id;

   private String objectIdentity;

   @Id
   public Integer getId()
   {
      return id;
   }

   public void setId(Integer id)
   {
      this.id = id;
   }

   public String getObjectIdentity()
   {
      return objectIdentity;
   }

   public void setObjectIdentity(String objectIdentity)
   {
      this.objectIdentity = objectIdentity;
   }
}
