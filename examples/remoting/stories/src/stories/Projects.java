package stories;

import javax.ejb.Local;

@Local
public interface Projects
{
   public Project getProject();
   public void createStory();
}
