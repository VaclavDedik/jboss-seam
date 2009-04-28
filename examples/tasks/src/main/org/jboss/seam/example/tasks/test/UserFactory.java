package org.jboss.seam.example.tasks.test;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.tasks.UserHome;
import org.jboss.seam.example.tasks.entity.User;

/**
 * Ugly hack that allows the tests tu run until JBSEAM-4152 is resolved.
 * @author Jozef Hartinger
 *
 */

@Name("userFactory")
public class UserFactory
{

   @In private UserHome userHome;
   
   @Factory(autoCreate=true, value="user")
   public User getDemo() {
      userHome.setId("demo");
      return userHome.find();
   }
   
}
