<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="t"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
 <f:view>
  <head>
   <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
   <title>
     <h:outputText value="#{messages.Create} #{messages.Project}" rendered="#{projectEditor.new}"/>
     <h:outputText value="#{messages.Update}/#{messages.Delete} #{messages.Project}" rendered="#{projectEditor.new}"/>
   </title>
   <style type="text/css" media="all">
    @import "style/default/screen.css";
   </style>
  </head>
  <body>
   <h:form>
   
     <h1>
         <h:outputText value="#{messages.Create} #{messages.Project}" rendered="#{projectEditor.new}"/>
         <h:outputText value="#{messages.Update}/#{messages.Delete} #{messages.Project}" rendered="#{!projectEditor.new}"/>
     </h1>
     
     <%@ include file="switcher.jsp" %> 
	
     <div class="rvgFind">
     <fieldset class="rvgFieldSet">
       <legend><h:outputText value="#{messages.Project} #{messages.Attributes}"/></legend>
       
       <span class="rvgInputs">
         <span class="rvgMessage"><h:messages globalOnly="true"/></span>
         <h:outputLabel value="#{messages.Project_name}" for="name">
           <h:inputText value="#{projectEditor.instance.name}" id="name" disabled="#{!projectEditor.new}"/>
           <span class="rvgMessage"><h:message for="name"/></span>
         </h:outputLabel>
         <h:outputLabel value="#{messages.Project_description}" for="description">
           <h:inputText value="#{projectEditor.instance.description}" id="description"/>
           <span class="rvgMessage"><h:message for="description"/></span>
         </h:outputLabel>
       </span>

       <span class="rvgActions">
         <h:commandButton type="submit" value="#{messages.Create}" action="#{projectEditor.create}" rendered="#{projectEditor.new}"/>
         <h:commandButton type="submit" value="#{messages.Update}" action="#{projectEditor.update}" rendered="#{!projectEditor.new}"/>
         <h:commandButton type="submit" value="#{messages.Delete}" action="#{projectEditor.delete}" rendered="#{!projectEditor.new}"/>
         <h:commandButton type="submit" value="#{messages.Done}" action="#{projectEditor.done}"/>
       </span>
     
     </fieldset>
     </div>
    
        <div class="rvgResults">
          <h2><h:outputText value="#{messages.Project_issues}"/></h2>
          
          <h:outputText value="#{messages.No} #{messages.Project_issues}" rendered="#{issuesList.rowCount==0}"/>
          <h:dataTable value="#{issuesList}" var="child" rendered="#{issuesList.rowCount>0}" rowClasses="rvgRowOne,rvgRowTwo">
            <h:column>
              <f:facet name="header"><h:outputText value="#{messages.Issue_id}"/></f:facet>
              <h:outputText value="#{child.id}"/>
            </h:column>
            <h:column>
              <f:facet name="header"><h:outputText value="#{messages.Issue_shortDescription}"/></f:facet>
              <h:commandLink value="#{child.shortDescription}" action="#{issueEditor.selectIssue}"/>
            </h:column>
            <h:column>
              <f:facet name="header"><h:outputText value="#{messages.Issue_releaseVersion}"/></f:facet>
              <h:outputText value="#{child.releaseVersion}"/>
            </h:column>
            <h:column>
              <f:facet name="header"><h:outputText value="#{messages.Issue_description}"/></f:facet>
              <h:outputText value="#{child.description}"/>
            </h:column>
            <h:column>
              <f:facet name="header"><h:outputText value="#{messages.Issue_submitted}"/></f:facet>
              <h:outputText value="#{child.submitted}"/>
            </h:column>
            <h:column>
              <f:facet name="header"><h:outputText value="#{messages.Action}"/></f:facet>
              <h:commandButton action="#{issueEditor.selectIssue}" value="#{messages.View} #{messages.Issue}"/>
            </h:column>
          </h:dataTable>

          <span class="rvgPage">
            <h:commandButton type="submit" value="#{messages.Create} #{messages.Issue}" action="#{issueEditor.createIssue}" 
              rendered="#{!projectEditor.new}"/>
          </span>

        </div>

        <div class="rvgResults">
          <h2><h:outputText value="#{messages.Project_developers}"/></h2>
          
          <h:outputText value="#{messages.No} #{messages.Project_developers}" rendered="#{empty projectEditor.developers}"/>
          <h:dataTable value="#{projectEditor.developers}" var="child" rendered="#{not empty projectEditor.developers}" 
                rowClasses="rvgRowOne,rvgRowTwo">
            <h:column>
              <f:facet name="header"><h:outputText value="#{messages.User_username}"/></f:facet>
              <h:outputText value="#{child.username}"/>
            </h:column>
            <h:column>
              <f:facet name="header"><h:outputText value="#{messages.User_name}"/></f:facet>
              <h:outputText value="#{child.name}"/>
            </h:column>
          </h:dataTable>

          <span class="rvgPage">
            <h:inputText value="#{projectEditor.developer}" rendered="#{!projectEditor.new}"/>
            <h:commandButton type="submit" value="#{messages.Add}" action="#{projectEditor.addDeveloper}" rendered="#{!projectEditor.new}"/>
          </span>

        </div>

       
   </h:form>

  </body>
 </f:view>
</html>