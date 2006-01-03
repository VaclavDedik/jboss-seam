package org.jboss.seam.example.issues;
// Generated Dec 27, 2005 10:28:30 AM by Hibernate Tools 3.1.0 beta3

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Interceptors;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.ejb.SeamInterceptor;


@Name("issueFinder")
@Stateful
@Scope(ScopeType.SESSION)
@Interceptors(SeamInterceptor.class)
public class IssueFinderBean implements IssueFinder {
    
    private Issue example = new Issue();
    public Issue getExample() {
        return example;
    }
    
    private int pageNumber = 0;
    private int pageSize = 25;
    public void setPageSize(int size) {
        pageSize = size;
    }
    public int getPageSize() {
        return pageSize;
    }
    
    public boolean isPreviousPage() {
        return issueList!=null && pageNumber>0;
    }
    public boolean isNextPage() {
        return issueList!=null && issueList.size()==pageSize;
    }
    
    @DataModel
    private List<Issue> issueList;

    @DataModelSelection
    private Issue selectedIssue;
    
    @In(create=true)
    private EntityManager entityManager;
    
    private void executeQuery() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        StringBuffer queryString = new StringBuffer();

        if ( example.getId() != null ) {
           queryString.append(" and issue.id = :id");
           parameters.put( "id", example.getId() );
        }

        if ( example.getShortDescription() != null && example.getShortDescription().length() > 0 ) {
           queryString.append(" and issue.shortDescription like :shortDescription");
           parameters.put( "shortDescription", '%' + example.getShortDescription() + '%' );
        }

        if ( example.getReleaseVersion() != null && example.getReleaseVersion().length()>0 ) {
           queryString.append(" and issue.releaseVersion = :releaseVersion");
           parameters.put( "releaseVersion", example.getReleaseVersion() );
        }

        if ( example.getDescription() != null && example.getDescription().length() > 0 ) {
           queryString.append(" and issue.description like :description");
           parameters.put( "description", '%' + example.getDescription() + '%' );
        }

        if ( example.getSubmitted() != null ) {
           queryString.append(" and issue.submitted = :submitted");
           parameters.put( "submitted", example.getSubmitted() );
        }


        if ( queryString.length()==0 ) {
           queryString.append("select issue from Issue issue");
        }
        else {
           queryString.delete(0, 4).insert(0, "select issue from Issue issue where");
        }
        
        if ( order!=null ) {
           queryString.append(" order by issue.").append(order);
           if (descending) queryString.append(" desc");
        }
        
        Query query = entityManager.createQuery(queryString.toString());
        for (Entry <String, Object> param: parameters.entrySet()) {
            query.setParameter( param.getKey(), param.getValue() );
        }
        issueList = (List<Issue>) query.setMaxResults(pageSize)
                .setFirstResult(pageSize*pageNumber)
                .getResultList();
    }
    
    public String findFirstPage() {
        pageNumber=0;
        executeQuery();
        return null;
    }
    
    public String findNextPage() {
        pageNumber++;
        executeQuery();
        return null;
    }
    
    public String findPreviousPage() {
        pageNumber--;
        executeQuery();
        return null;
    }
    
    public void refresh() {
        if (issueList!=null) executeQuery();
    }
    
    public String clear() {
        issueList=null;
        example = new Issue();
        return null;
    }
    
    public Issue getSelection() {
        return entityManager.merge( selectedIssue );
    }
        
    @Destroy @Remove
    public void destroy() {}
    
    private String order;
    private boolean descending = false;
    
    @RequestParameter
    private String orderBy;

    public String reorder() {
        if (orderBy.equals(order)) {
            descending = !descending;
        }
        else {
            descending = false;
        }
        order = orderBy;
        executeQuery();
        return null;
    }

}