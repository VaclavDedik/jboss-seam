package com.jboss.dvd.ejb;

import java.io.Serializable;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;

import org.jboss.seam.Seam;
import org.jboss.seam.annotations.JndiName;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateless
@Name("login")
@JndiName("com.jboss.dvd.ejb.Login")
@Interceptor(SeamInterceptor.class)
public class LoginAction 
    implements Login,
               Serializable
{
    public String logout() {
        Seam.invalidateSession();
        return "done";
    }

}
