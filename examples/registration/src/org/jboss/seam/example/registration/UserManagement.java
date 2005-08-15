package org.jboss.seam.example.registration;

import javax.ejb.Local;


/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
@Local
public interface UserManagement
{
   public String register();
   public String retrieve();
   public String changePassword();

   public String getUsername();
   public void setUsername(String userName);
}


