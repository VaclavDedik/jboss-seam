package stories;

import javax.ejb.Local;

import org.jboss.seam.annotations.WebRemote;

@Local
public interface Stories
{

   @WebRemote
   void reorder(int[] storyIds);
   @WebRemote
   public boolean deleteStory(int storyId);
   @WebRemote
   public void setName(int storyId, String name);
   @WebRemote
   public void setBody(int storyId, String name);

}