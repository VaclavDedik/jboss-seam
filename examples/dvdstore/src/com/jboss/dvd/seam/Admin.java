/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Inheritance(discriminatorValue="admin")
public class Admin
    extends User
    implements Serializable
{
    
}
