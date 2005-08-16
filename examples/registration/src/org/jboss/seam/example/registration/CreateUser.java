package org.jboss.seam.example.registration;

import javax.ejb.Local;


/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

/**
 * @author Gavin King
 */
@Local
public interface CreateUser
{
   public String createUser();
}


