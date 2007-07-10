package org.jboss.seam.example.remoting;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.*;
import org.jboss.seam.annotations.remoting.WebRemote;

//@Stateless
@Name("helloAction")
public class HelloAction implements HelloLocal {
    @RequestParameter String foo;

    @WebRemote
    public String sayHello(String name) {
        System.out.println("Foo=" + foo);
        return "Hello, " + name;
    }
}
