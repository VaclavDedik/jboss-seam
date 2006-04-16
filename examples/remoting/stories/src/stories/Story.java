package stories;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Story implements Comparable
{
   @Id @GeneratedValue
   private int id;
   private String name;
   private int points;
   private String body;
   @Column(name="posn")
   private int position;
   @ManyToOne
   private Project project;
   
   public Story() {}

   public Story(Project project, String name, int points, String body, int pos)
   {
      this.name = name;
      this.points = points;
      this.body = body;
      this.position = pos;
      this.project = project;
   }
   
   public String getBody()
   {
      return body;
   }
   public void setBody(String body)
   {
      this.body = body;
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
   public int getPoints()
   {
      return points;
   }
   public void setPoints(int points)
   {
      this.points = points;
   }

   public int getPosition()
   {
      return position;
   }

   public void setPosition(int position)
   {
      this.position = position;
   }

   public int compareTo(Object o)
   {
      Story that = (Story) o;
      return new Integer(position).compareTo(that.position);
   }

   public Project getProject()
   {
      return project;
   }

   public void setProject(Project project)
   {
      this.project = project;
   }
}
