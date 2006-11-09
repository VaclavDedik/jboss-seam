package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.jboss.seam.security.acl.AclProvider.RecipientType;

/**
 * Defines permissions for an object
 *
 * @author Shane Bryzak
 */
@Entity
public class AclPermission implements Serializable
{
  private Integer id;
  private AclObjectIdentity identity;
  private RecipientType recipientType;
  private String recipient;
  private int mask;

  @Id
  public Integer getId()
  {
    return id;
  }

  public void setId(Integer id)
  {
    this.id = id;
  }

  @ManyToOne
  public AclObjectIdentity getIdentity()
  {
    return identity;
  }

  public void setIdentity(AclObjectIdentity identity)
  {
    this.identity = identity;
  }

  public RecipientType getRecipientType()
  {
    return recipientType;
  }

  public void setRecipientType(RecipientType recipientType)
  {
    this.recipientType = recipientType;
  }

  public String getRecipient()
  {
    return recipient;
  }

  public void setRecipient(String recipient)
  {
    this.recipient = recipient;
  }

  public int getMask()
  {
    return mask;
  }

  public void setMask(int mask)
  {
    this.mask = mask;
  }
}
