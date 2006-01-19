<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="t"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
 <f:view>
 <f:loadBundle basename="messages" var="msg"/>
  <head>
   <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
   <title>
     <h:outputText value="#{msg.Create} #{msg.Issue}" rendered="#{issueEditor.new}"/>
     <h:outputText value="#{msg.Update}/#{msg.Delete} #{msg.Issue}" rendered="#{!issueEditor.new}"/>
   </title>
   <style type="text/css" media="all">
    @import "style/default/screen.css";
   </style>
  </head>
  <body>
   <h:form>
   
     <h1>
         <h:outputText value="#{msg.Create} #{msg.Issue}" rendered="#{issueEditor.new}"/>
         <h:outputText value="#{msg.Update}/#{msg.Delete} #{msg.Issue}" rendered="#{!issueEditor.new}"/>
     </h1>
     
     <%@ include file="switcher.jsp" %> 
	
     <div class="rvgFind">
     <fieldset class="rvgFieldSet">
       <legend><h:outputText value="#{msg.Issue} #{msg.Attributes}"/></legend>
       
       <span class="rvgInputs">
         <span class="rvgMessage"><h:messages globalOnly="true"/></span>
         <h:outputLabel value="#{msg.Issue_id}" for="id">
           <h:inputText value="#{issueEditor.instance.id}" id="id" disabled="true"/>
           <span class="rvgMessage"><h:message for="id"/></span>
         </h:outputLabel>
         <h:outputLabel value="#{msg.Issue_status}" for="status">
           <h:inputText value="#{issueEditor.instance.status}" id="status" disabled="true"/>
           <span class="rvgMessage"><h:message for="status"/></span>
         </h:outputLabel>
         <h:outputLabel value="#{msg.Issue_shortDescription}" for="shortDescription">
           <h:inputText value="#{issueEditor.instance.shortDescription}" id="shortDescription"/>
           <span class="rvgMessage"><h:message for="shortDescription"/></span>
         </h:outputLabel>
         <h:outputLabel value="#{msg.Issue_releaseVersion}" for="releaseVersion">
           <h:inputText value="#{issueEditor.instance.releaseVersion}" id="releaseVersion"/>
           <span class="rvgMessage"><h:message for="releaseVersion"/></span>
         </h:outputLabel>
         <h:outputLabel value="#{msg.Issue_description}" for="description">
           <h:inputTextarea value="#{issueEditor.instance.description}" id="description" rows="8"/>
           <span class="rvgMessage"><h:message for="description"/></span>
         </h:outputLabel>
         <h:outputLabel value="#{msg.Issue_submitted}" for="submitted">
           <h:inputText value="#{issueEditor.instance.submitted}" id="submitted" disabled="true">
               <f:convertDateTime type="both" dateStyle="short"/>
           </h:inputText>
           <span class="rvgMessage"><h:message for="submitted"/></span>
         </h:outputLabel>
       </span>

       <span class="rvgActions">
         <h:commandButton type="submit" value="#{msg.Create}" action="#{issueEditor.create}" rendered="#{issueEditor.new}"/>
         <h:commandButton type="submit" value="#{msg.Update}" action="#{issueEditor.update}" rendered="#{issueEditor.open && !issueEditor.new}"/>
         <h:commandButton type="submit" value="#{msg.Delete}" action="#{issueEditor.delete}" rendered="#{!issueEditor.new}"/>
         <h:commandButton type="submit" value="#{msg.Done}" action="#{issueEditor.done}"/>
         <h:commandButton type="submit" value="#{msg.Resolve} #{msg.Issue}" action="#{issueEditor.resolve}" rendered="#{issueEditor.open && !issueEditor.new}"/>
       </span>
     
     </fieldset>
     </div>
    
        <div class="rvgResults">
           <h2><h:outputText value="#{msg.Issue_user}"/></h2>
           <h:dataTable var="parent" value="#{issueEditor.instance.user}" rowClasses="rvgRowOne,rvgRowTwo">
               <h:column>
                 <f:facet name="header"><h:outputText value="#{msg.User_username}"/></f:facet>
                 <h:outputText value="#{parent.username}"/>
               </h:column>
               <h:column>
                 <f:facet name="header"><h:outputText value="#{msg.User_name}"/></f:facet>
                 <h:outputText value="#{parent.name}"/>
               </h:column>
           </h:dataTable>
        </div>
        
        <div class="rvgResults">
           <h2><h:outputText value="#{msg.Issue_project}"/></h2>
           <h:outputText value="#{msg.No} #{msg.Issue_project}" rendered="#{issueEditor.instance.project == null}"/>
           <h:dataTable var="parent" value="#{issueEditor.instance.project}" 
                   rendered="#{issueEditor.instance.project != null}" rowClasses="rvgRowOne,rvgRowTwo">
               <h:column>
                 <f:facet name="header"><h:outputText value="#{msg.Project_name}"/></f:facet>
                 <h:outputText value="#{parent.name}"/>
               </h:column>
               <h:column>
                 <f:facet name="header"><h:outputText value="#{msg.Project_description}"/></f:facet>
                 <h:outputText value="#{parent.description}"/>
               </h:column>
           </h:dataTable>

          <span class="rvgPage">
            <h:commandButton type="submit" value="#{msg.Select} #{msg.Project}" action="#{issueProjectSelector.selectProject}" />
          </span>

        </div>
        
        <div class="rvgResults">
           <h2><h:outputText value="#{msg.Issue_assigned}"/></h2>
           <h:outputText value="#{msg.No} #{msg.Issue_assigned}" rendered="#{issueEditor.instance.assigned == null}"/>
           <h:dataTable var="parent" value="#{issueEditor.instance.assigned}" 
                   rendered="#{issueEditor.instance.assigned != null}"  rowClasses="rvgRowOne,rvgRowTwo">
               <h:column>
                 <f:facet name="header"><h:outputText value="#{msg.User_username}"/></f:facet>
                 <h:outputText value="#{parent.username}"/>
               </h:column>
               <h:column>
                 <f:facet name="header"><h:outputText value="#{msg.User_name}"/></f:facet>
                 <h:outputText value="#{parent.name}"/>
               </h:column>
           </h:dataTable>

          <span class="rvgPage">
            <h:inputText value="#{issueEditor.developer}" rendered="#{!issueEditor.new}"/>
            <h:commandButton type="submit" value="#{msg.Assign}" action="#{issueEditor.assignDeveloper}" rendered="#{!issueEditor.new}" />
            <h:commandButton type="submit" value="#{msg.Unassign}" action="#{issueEditor.unassignDeveloper}" rendered="#{!issueEditor.new}" />
          </span>

        </div>

        <div class="rvgResults">
          <h2><h:outputText value="#{msg.Issue_comments}"/></h2>
          
          <h:outputText value="#{msg.No} #{msg.Issue_comments}" rendered="#{commentsList.rowCount==0}"/>
          <h:dataTable value="#{commentsList}" var="child" rendered="#{commentsList.rowCount>0}" rowClasses="rvgRowOne,rvgRowTwo">
            <h:column>
              <f:facet name="header"><h:outputText value="#{msg.Comment_text}"/></f:facet>
              <h:outputText value="#{child.text}"/>
            </h:column>
            <h:column>
              <f:facet name="header"><h:outputText value="#{msg.Comment_submitted}"/></f:facet>
              <h:outputText value="#{child.submitted}"/>
            </h:column>
            <h:column>
              <f:facet name="header"><h:outputText value="#{msg.Action}"/></f:facet>
              <h:commandButton action="#{commentEditor.selectComment}" value="#{msg.View} #{msg.Comment}"/>
            </h:column>
          </h:dataTable>

          <span class="rvgPage">
            <h:commandButton type="submit" value="#{msg.Create} #{msg.Comment}" action="#{commentEditor.createComment}" 
              rendered="#{!issueEditor.new}"/>
          </span>

        </div>

       
   </h:form>

  </body>
 </f:view>
</html>