package stories;

import static org.jboss.seam.ScopeType.EVENT;

import javax.ejb.Interceptors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.ejb.SeamInterceptor;

@Name("projects")
@Stateless
@Interceptors(SeamInterceptor.class)
public class ProjectsBean implements Projects
{
   @PersistenceContext
   private EntityManager em;
   
   @RequestParameter
   private Integer id;
   
   @Out(scope=EVENT)
   private Project project;
   
   @Out(scope=EVENT, required=false)
   private Story story;
   
   @Factory("project")
   public Project getProject()
   {
      project = em.find(Project.class, id);
      project.getStories().size();
      return project;
   }
   
   public void createStory()
   {
      Project project = getProject();
      story = new Story(project, "Enter name here", 1, "Enter body text here", project.getStories().size());
      em.persist(story);
   }
}
