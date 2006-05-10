/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 

package com.jboss.dvd.seam;

public interface Login 
{
    public String getUserName();
    public void setUserName(String username);
    public String getPassword();
    public void setPassword(String password);

    public String login();
    public String logout();

    public boolean isLoggedIn();
}
