package org.jboss.seam.mock;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.jboss.seam.security.acl.AclManager.RecipientType;

/**
 * Defines permissions for an object
 *
 * @author Shane Bryzak
 */
@Entity
public class MockAclPermission implements Serializable
{
  private Integer id;
  private MockAclObjectIdentity identity;
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
  public MockAclObjectIdentity getIdentity()
  {
    return identity;
  }

  public void setIdentity(MockAclObjectIdentity identity)
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
