package org.jboss.seam.example.security;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.DefinePermissions;
import org.jboss.seam.annotations.security.AclProvider;

/**
 * <p>PROPRIETARY/CONFIDENTIAL Use of this product is subject to license terms.
 * Copyright (c) 2006 Symantec Corporation. All rights reserved.</p>
 *
 * @author Shane Bryzak
 * @version 1.0
 */
@Name("customer")
@DefinePermissions(permissions = {
  @AclProvider(action = "modify", provider = "customerAclProvider"),
  @AclProvider(action = "view", provider = "persistentAclProvider", mask = 0x02)
})
public class Customer
{
  private String name;
  private boolean readonly;

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public boolean isReadonly()
  {
    return readonly;
  }

  public void setReadonly(boolean readonly)
  {
    this.readonly = readonly;
  }
}
