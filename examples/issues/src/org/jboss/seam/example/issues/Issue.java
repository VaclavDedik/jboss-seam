package org.jboss.seam.example.issues;
// Generated Dec 27, 2005 10:28:30 AM by Hibernate Tools 3.1.0 beta3

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;


@Entity
public class Issue implements Serializable {

     private Integer id;
     private User user;
     private Project project;
     private String shortDescription;
     private String releaseVersion;
     private String description;
     private Date submitted;
     private Set<Comment> comments = new HashSet<Comment>(0);
     private User assigned;
     private IssueStatus status = IssueStatus.OPEN;

    @Id  @GeneratedValue
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    @ManyToOne @NotNull
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    @ManyToOne @NotNull
    public Project getProject() {
        return this.project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }
    
    @Length(max=50) @NotNull
    public String getShortDescription() {
        return this.shortDescription;
    }
    
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    
    @NotNull @Length(max=9) 
    @Pattern(regex="\\d*(\\.\\d*){0,2}")
    public String getReleaseVersion() {
        return this.releaseVersion;
    }
    
    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }
    
    @Length(max=1000) @NotNull
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @NotNull
    public Date getSubmitted() {
        return this.submitted;
    }
    
    public void setSubmitted(Date submitted) {
        this.submitted = submitted;
    }
    
    @OneToMany(mappedBy="issue")
    @OrderBy("submitted")
    public Set<Comment> getComments() {
        return this.comments;
    }
    
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public User getAssigned() {
       return assigned;
    }

    public void setAssigned(User assigned) {
       this.assigned = assigned;
    }

    public IssueStatus getStatus() {
       return status;
    }

    public void setStatus(IssueStatus status) {
       this.status = status;
    }

}
