package org.jboss.seam.example.tasks;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.tasks.entity.User;
import org.jboss.seam.framework.EntityHome;

@Name("userHome")
@AutoCreate
public class UserHome extends EntityHome<User>
{

}
