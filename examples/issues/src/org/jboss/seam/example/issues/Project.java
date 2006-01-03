package org.jboss.seam.example.issues;
// Generated Dec 27, 2005 10:28:30 AM by Hibernate Tools 3.1.0 beta3

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Formula;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
public class Project implements Serializable {

    private String name;
    private String description;
    private Set<Issue> issues = new HashSet<Issue>(0);
    private Set<User> developers = new HashSet<User>(0);
    private int issueCount;

    @Id @NotNull
    @Length(max=20)
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @NotNull
    @Length(max=200)
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    @OneToMany(mappedBy="project")
    public Set<Issue> getIssues() {
        return this.issues;
    }
    
    public void setIssues(Set<Issue> issues) {
        this.issues = issues;
    }
    
    @Formula("(select count(*) from issue i where i.project_name = name)")
    public int getIssueCount()
    {
       return issueCount;
    }
    
    @ManyToMany 
    public Set<User> getDevelopers() {
       return developers;
    }

    public void setDevelopers(Set<User> developers) {
       this.developers = developers;
    }

   public void setIssueCount(int issueCount) {
      this.issueCount = issueCount;
   }

}
