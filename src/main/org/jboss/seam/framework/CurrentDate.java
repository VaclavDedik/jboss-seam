package org.jboss.seam.framework;

import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

@Name("currentDate")
@Scope(ScopeType.STATELESS)
public class CurrentDate
{
   @Unwrap 
   public Date getCurrentDate()
   {
      return new java.sql.Date( System.currentTimeMillis() );
   }
}
