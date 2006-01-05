package org.jboss.seam.example.issues;
// Generated Dec 27, 2005 10:28:30 AM by Hibernate Tools 3.1.0 beta3

import static javax.ejb.TransactionAttributeType.NOT_SUPPORTED;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.ejb.Interceptors;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.hibernate.validator.Valid;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Outcome;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.ejb.SeamInterceptor;


@Name("projectEditor")
@Stateful
@Interceptors(SeamInterceptor.class)
public class ProjectEditorBean implements ProjectEditor {

    @In(create=true)
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
    
    @In(required=false)
    private transient ProjectFinder projectFinder;

    @In
    private transient ResourceBundle resourceBundle;
    
    @Begin
    @IfInvalid(outcome=Outcome.REDISPLAY)
    public String create() {
       if ( entityManager.find(Project.class, project.getName())!=null )
       {
          FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(
                      resourceBundle.getString("Project_name") + " " +
                      resourceBundle.getString("AlreadyExists")
                   )
             );
          return null;
       }
       entityManager.persist(project);
       isNew = false;
       refreshFinder();
       return "editProject";
    }
    
    public String getDescription() {
       return "Project [" + project.getName() + "]";
    }

    @IfInvalid(outcome=Outcome.REDISPLAY)
    public String update() {
       refreshFinder();
       return null;
    }

    @End
    public String delete() {
       entityManager.remove(project);
       refreshFinder();
       return "home";
    }

    @End
    public String done() {
       if (!isNew) entityManager.refresh(project);
       return "home";
    }
    
    private void refreshFinder() {
       if (projectFinder!=null) projectFinder.refresh();
    }

    @DataModel
    public List getIssuesList() {
       return project == null || project.getIssues()==null ?
             null : new ArrayList( project.getIssues() );
    }

    @DataModelSelection
    private Issue selectedIssue;

    public Issue getSelectedIssue()
    {
       return selectedIssue;
    }
    
    private String developer;
    
    public List<User> getDevelopers()
    {
       return project.getDevelopers()==null ? null : new ArrayList<User>( project.getDevelopers() );
    }
    
    public void setDeveloper(String developer)
    {
       this.developer = developer;
    }
    
    public String getDeveloper()
    {
       return developer;
    }
    
    public String addDeveloper()
    {
       User user = entityManager.find(User.class, developer);
       if (user==null)
       {
          FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(
                      resourceBundle.getString("User_username") + " " +
                      resourceBundle.getString("NotFound")
                   )
             );
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