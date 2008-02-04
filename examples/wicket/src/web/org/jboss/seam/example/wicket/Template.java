package org.jboss.seam.example.wicket;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.jboss.seam.annotations.In;
import org.jboss.seam.example.wicket.action.User;
import org.jboss.seam.security.Identity;

public class Template extends Border
{

   @In 
   private User user;
   
   @In
   private Identity identity;
   
   public Template(String id)
   {
      super(id);
      add(new BookmarkablePageLink("search", Main.class));
      add(new BookmarkablePageLink("settings", Password.class));
      add(new Link("logout")
      {
         @Override
         public void onClick()
         {
            identity.logout();
            setResponsePage(Home.class);
         }
      });
      add(new Label("userName", user.getName()));
   }

}
