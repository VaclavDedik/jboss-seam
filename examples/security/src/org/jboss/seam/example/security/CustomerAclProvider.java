package org.jboss.seam.example.security;

import java.security.Principal;
import java.security.acl.Permission;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Authentication;
import org.jboss.seam.security.SeamPermission;
import org.jboss.seam.security.acl.AbstractAclProvider;

/**
 * <p>PROPRIETARY/CONFIDENTIAL Use of this product is subject to license terms.
 * Copyright (c) 2006 Symantec Corporation. All rights reserved.</p>
 *
 * @author Shane Bryzak
 * @version 1.0
 */
@Name("customerAclProvider")
public class CustomerAclProvider extends AbstractAclProvider
{
  public Map<Principal,Set<Permission>> getPermissions(Object obj)
  {
    Map<Principal, Set<Permission>> perms = new HashMap<Principal,Set<Permission>>();
    perms.put(Authentication.instance(), getPermissions(obj, Authentication.instance()));
    return perms;
  }

  public Set<Permission> getPermissions(Object obj, Principal principal)
  {
    if (obj instanceof Customer && !((Customer) obj).isReadonly())
    {
      Set<Permission> perms = new HashSet<Permission>();
      perms.add(new SeamPermission("customer", "modify"));
      return perms;
    }
    return null;
  }
}
