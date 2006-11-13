package org.jboss.seam.mock;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.AclProvider;
import org.jboss.seam.annotations.security.DefinePermissions;

/**
 * Used by security unit tests
 *
 * @author Shane Bryzak
 */
@Name("mockSecureEntity")
@DefinePermissions(
    permissions = {
  @AclProvider(action = "read", provider = "persistentAclProvider", mask = 0x01),
  @AclProvider(action = "delete", provider = "persistentAclProvider", mask = 0x02),
  @AclProvider(action = "special", provider = "persistentAclProvider", mask = 0x04)
  }
)
@Entity
public class MockSecureEntity implements Serializable
{
  private Integer id;
  private String value;

  @Id
  public Integer getId()
  {
    return id;
  }

  public void setId(Integer id)
  {
    this.id = id;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }
}
