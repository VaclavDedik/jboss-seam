package stories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
public class Project
{
   private String name;
   @Id private int id;
   @OneToMany(mappedBy="project") 
   @OrderBy("position")
   private List<Story> stories = new ArrayList<Story>();
   

   public List getStories()
   {
      return stories;
   }

   public Collection getBacklog()
   {
      return new ArrayList(stories);
   }
   
   public int getRemainingWork()
   {
      return 12;
   }

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

}
