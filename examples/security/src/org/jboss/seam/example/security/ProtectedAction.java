package org.jboss.seam.example.security;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.jboss.seam.annotations.Name;
import org.jboss.annotation.security.SecurityDomain;

/**
 * <p>PROPRIETARY/CONFIDENTIAL Use of this product is subject to license terms.
 * Copyright (c) 2006 Symantec Corporation. All rights reserved.</p>
 *
 * @author Shane Bryzak
 * @version 1.0
 */
@Stateless
@Name("protectedAction")
@SecurityDomain("seam")
public class ProtectedAction implements ProtectedLocal
{
  @RolesAllowed("admin")
  public String foo()
  {
    System.out.println("foo() called");
    return "success";
  }
}
