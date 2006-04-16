package stories;

import javax.ejb.Interceptors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.ejb.SeamInterceptor;

@Name("stories")
@Stateless
@Interceptors(SeamInterceptor.class)
public class StoriesBean implements Stories
{
   
   @PersistenceContext 
   private EntityManager em;
   
   public void reorder(int[] storyIds)
   {
      int pos=0;
      for (int id: storyIds)
      {
         Story story = getStory(id);
         if ( story!=null ) story.setPosition(pos++);
      }
   }
   
   private Story getStory(int storyId)
   {
      return em.find(Story.class, storyId);
   }
   
   public boolean deleteStory(int storyId)
   {
      em.remove( getStory(storyId) );
      return true;
   }

   public void setName(int storyId, String name)
   {
      getStory(storyId).setName(name);
   }
   
   public void setBody(int storyId, String name)
   {
      getStory(storyId).setBody(name);
   }
   
}
