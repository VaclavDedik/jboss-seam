package org.jboss.seam.example.issues;
// Generated Dec 27, 2005 10:28:30 AM by Hibernate Tools 3.1.0 beta3

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
public class Comment implements Serializable {

    private Integer id;
    private User user;
    private Issue issue;
    private String text;
    private Date submitted;

    @Id(generate=GeneratorType.AUTO)
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
    public Issue getIssue() {
        return this.issue;
    }
    
    public void setIssue(Issue issue) {
        this.issue = issue;
    }
    
    @Length(max=500) @NotNull
    public String getText() {
        return this.text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    @NotNull
    public Date getSubmitted() {
        return this.submitted;
    }
    
    public void setSubmitted(Date submitted) {
        this.submitted = submitted;
    }

}
