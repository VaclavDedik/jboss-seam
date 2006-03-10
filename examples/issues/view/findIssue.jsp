<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="t"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<f:view>
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <title><h:outputText value="#{messages.Find} #{messages.Issue}"/></title>
  <style type="text/css" media="all">
	@import "style/default/screen.css";
  </style>
 </head>
 <body>
 
  <h1><h:outputText value="#{messages.Find} #{messages.Issue}"/></h1>
 
   <h:form>

     <%@ include file="switcher.jsp" %> 
	
     <div class="rvgFind">
       <fieldset class="rvgFieldSet">
         <legend><h:outputText value="#{messages.Issue} #{messages.SearchCriteria}"/></legend>
         
         <span class="rvgInputs">
           <h:outputLabel value="#{messages.Issue_shortDescription}" for="shortDescription">
             <h:inputText value="#{issueFinder.example.shortDescription}" id="shortDescription"/>
           </h:outputLabel>
           <h:outputLabel value="#{messages.Issue_releaseVersion}" for="releaseVersion">
             <h:inputText value="#{issueFinder.example.releaseVersion}" id="releaseVersion"/>
           </h:outputLabel>
           <h:outputLabel value="#{messages.Issue_description}" for="description">
             <h:inputText value="#{issueFinder.example.description}" id="description"/>
           </h:outputLabel>
           <h:outputLabel value="#{messages.Issue_submitted}" for="submitted">
             <h:inputText value="#{issueFinder.example.submitted}" id="submitted">
               <f:convertDateTime type="both" dateStyle="short"/>
             </h:inputText>
           </h:outputLabel>
           <h:outputLabel value="#{messages.PageSize}" for="pageSize">
             <h:inputText value="#{issueFinder.pageSize}" id="pageSize"/>
           </h:outputLabel>
         </span>
         
         <span class="rvgActions">
           <h:commandButton type="submit" value="#{messages.Clear}" action="#{issueFinder.clear}"/>
           <h:commandButton type="submit" value="#{messages.Find}" action="#{issueFinder.findFirstPage}"/>
	     </span>
	     
       </fieldset>
     </div>
	 
	 <div class="rvgResults">
     
	 <span class="rvgResultsNone">
	   <h:outputText value="#{messages.EnterSearchCriteria}" rendered="#{issueList==null}"/>
	   <h:outputText value="#{messages.No} #{messages.Issue} #{messages.MatchedSearchCriteria}" rendered="#{issueList.rowCount==0 && !issueFinder.previousPage}"/>
	 </span>
	 
	 <h:dataTable value="#{issueList}" var="issue" rendered="#{issueList.rowCount>0}" 
	       rowClasses="rvgRowOne,rvgRowTwo" headerClass="rvgOrder">
		<h:column>
			<f:facet name="header">
			    <h:commandLink value="#{messages.Issue_id}" action="#{issueFinder.reorder}">
			       <f:param name="orderBy" value="id"/>
			    </h:commandLink>
			</f:facet>
			<h:outputText value="#{issue.id}"/>
		</h:column>
		<h:column>
			<f:facet name="header">
			    <h:commandLink value="#{messages.Issue_status}" action="#{issueFinder.reorder}">
			       <f:param name="orderBy" value="status"/>
			    </h:commandLink>
			</f:facet>
			<h:outputText value="#{issue.status}"/>
		</h:column>
		<h:column>
			<f:facet name="header">
			   <h:outputText value="#{messages.Issue_user}"/>
			</f:facet>
			<h:outputText value="#{issue.user.username}"/>
		</h:column>
		<h:column>
			<f:facet name="header">
			   <h:outputText value="#{messages.Issue_project}"/>
			</f:facet>
			<h:outputText value="#{issue.project.name}"/>
		</h:column>
		<h:column>
			<f:facet name="header">
			    <h:commandLink value="#{messages.Issue_shortDescription}" action="#{issueFinder.reorder}">
			       <f:param name="orderBy" value="shortDescription"/>
			    </h:commandLink>
			</f:facet>
			<h:commandLink value="#{issue.shortDescription}" action="#{issueEditor.select}"/>
		</h:column>
		<h:column>
			<f:facet name="header">
			    <h:commandLink value="#{messages.Issue_releaseVersion}" action="#{issueFinder.reorder}">
			       <f:param name="orderBy" value="releaseVersion"/>
			    </h:commandLink>
			</f:facet>
			<h:outputText value="#{issue.releaseVersion}"/>
		</h:column>
		<h:column>
			<f:facet name="header">
			    <h:commandLink value="#{messages.Issue_submitted}" action="#{issueFinder.reorder}">
			       <f:param name="orderBy" value="submitted"/>
			    </h:commandLink>
			</f:facet>
			<h:outputText value="#{issue.submitted}"/>
		</h:column>
		<h:column>
			<f:facet name="header"><h:outputText value="#{messages.Action}"/></f:facet>
			<h:commandButton action="#{issueEditor.select}" value="#{messages.View}"/>
		</h:column>
	 </h:dataTable>

	 <span class="rvgPage">
	   <h:commandButton action="#{issueFinder.findPreviousPage}" value="#{messages.PreviousPage}" disabled="#{!issueFinder.previousPage}" />
	   <h:commandButton action="#{issueFinder.findNextPage}" value="#{messages.NextPage}" disabled="#{!issueFinder.nextPage}" />
	 </span>
	 
	 </div>
	
   </h:form>
   

 </body>
</f:view>
</html>