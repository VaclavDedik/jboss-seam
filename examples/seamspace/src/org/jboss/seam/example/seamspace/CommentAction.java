package org.jboss.seam.example.seamspace;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;

@Name("commentAction")
@Stateful
public class CommentAction implements CommentLocal
{

   @Remove @Destroy
   public void destroy() { } 
}
