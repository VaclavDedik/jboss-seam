package org.jboss.seam.example.issues;
// Generated Dec 27, 2005 10:28:30 AM by Hibernate Tools 3.1.0 beta3

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;


@Name("projectFinder")
@Stateful
@Scope(ScopeType.EVENT)
public class ProjectFinderBean implements ProjectFinder {
    
    @DataModel(scope=ScopeType.PAGE)
    private List<Project> projectList;

    @DataModelSelection
    private Project selectedProject;
    
    @In
    private EntityManager entityManager;
    
    @Factory("projectList")
    public void findProjects() {
        executeQuery();
    }

    private void executeQuery() {
       projectList = entityManager.createQuery("select project from Project project order by project.name")
            .getResultList();
    }
        
    public void refresh() {
        if (projectList!=null) executeQuery();
    }
    
    public Project getSelection() {
        return entityManager.merge( selectedProject );
    }
        
    @Destroy @Remove
    public void destroy() {}
    
}