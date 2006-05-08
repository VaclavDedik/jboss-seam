//$Id$
package org.jboss.seam.test;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Stateful
@Name("ejb")
@Scope(ScopeType.EVENT)
public class EjbBean implements Ejb
{
   public void foo() {}
   @Remove @Destroy
   public void destroy() {}
}
