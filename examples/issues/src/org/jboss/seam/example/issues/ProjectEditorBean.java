package org.jboss.seam.example.issues;
// Generated Dec 27, 2005 10:28:30 AM by Hibernate Tools 3.1.0 beta3

import static javax.ejb.TransactionAttributeType.NOT_SUPPORTED;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;

import org.hibernate.validator.Valid;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Outcome;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.FacesMessages;

@Name("projectEditor")
@Stateful
public class ProjectEditorBean implements ProjectEditor {

    @In
    private EntityManager entityManager;

    @Valid
    private Project project = new Project();
    
    @TransactionAttribute(NOT_SUPPORTED)
    public Project getInstance() {
       return project;
    }
    public void setInstance(Project instance) {
       isNew = false;
       this.project = instance;
    }

    private boolean isNew = true;
    
    @TransactionAttribute(NOT_SUPPORTED)
    public boolean isNew() {
       return isNew;
    }
    
    @Restrict("#{identity.loggedIn}")
    @Begin(join=true)
    @IfInvalid(outcome=Outcome.REDISPLAY)
    public String create() {
       if ( entityManager.find(Project.class, project.getName())!=null )
       {
          FacesMessages.instance().addFromResourceBundle("ProjectAlreadyExists");
          return null;
       }
       entityManager.persist(project);
       isNew = false;
       refreshFinder();
       return "editProject";
    }
    
    @Restrict("#{identity.loggedIn}")
    @Begin
    public String createProject() {
       isNew = true;
       project = new Project();
       return "editProject";
    }
    
    @TransactionAttribute(NOT_SUPPORTED)
    public String getDescription() {
       return project==null || project.getName()==null ? 
             "New Project" : "Project [" + project.getName() + "]";
    }

    @Restrict("#{identity.loggedIn}")
    @IfInvalid(outcome=Outcome.REDISPLAY, refreshEntities=true)
    public String update() {
       refreshFinder();
       return null;
    }

    @End
    @Restrict("#{identity.loggedIn}")
    public String delete() {
       if ( project.getIssues().isEmpty() )
       {
          entityManager.remove(project);
          refreshFinder();
          return "home";
       }
       else
       {
          FacesMessages.instance().addFromResourceBundle("ProjectHasIssues");
          return null;
       }
    }

    @End
    public String done() {
       if (!isNew) entityManager.refresh(project);
       return "home";
    }
    
    private void refreshFinder() {
       Events.instance().raiseEvent("projectUpdate");
    }

    @DataModel
    public List getIssuesList() {
       return project==null || project.getIssues()==null ?
             null : new ArrayList( project.getIssues() );
    }

    @DataModelSelection
    private Issue selectedIssue;

    public Issue getSelectedIssue()
    {
       return selectedIssue;
    }
    
    @Out(scope=ScopeType.EVENT, required=false)
    private String developer;
    
    public List<User> getDevelopers()
    {
       return project.getDevelopers()==null ? 
             null : new ArrayList<User>( project.getDevelopers() );
    }
    
    @TransactionAttribute(NOT_SUPPORTED)
    public void setDeveloper(String developer)
    {
       this.developer = developer;
    }
    
    @TransactionAttribute(NOT_SUPPORTED)
    public String getDeveloper()
    {
       return developer;
    }
    
    public String addDeveloper()
    {
       User user = entityManager.find(User.class, developer);
       if (user==null)
       {
          FacesMessages.instance().addFromResourceBundle("UserNotFound");
       }
       else
       {
          project.getDevelopers().add(user);
          developer = null;
       }
       return null;
    }

    @Destroy @Remove
    public void destroy() {}

}