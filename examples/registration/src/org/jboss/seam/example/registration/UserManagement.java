package org.jboss.seam.example.registration;


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
public interface UserManagement
{
   public String register();
   public String retrieve();
   public String setPassword();

   public String getUsername();
   public void setUsername(String userName);
}


