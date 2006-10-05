package org.jboss.seam.framework;

import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

@Name("currentDatetime")
@Scope(ScopeType.STATELESS)
public class CurrentDatetime
{
   @Unwrap 
   public Date getCurrentDatetime()
   {
      return new java.sql.Timestamp( System.currentTimeMillis() );
   }
}
