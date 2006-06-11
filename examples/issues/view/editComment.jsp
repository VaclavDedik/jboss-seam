<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
 <f:view>
  <head>
   <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
   <title>
     <h:outputText value="#{messages.Create} #{messages.Comment}" rendered="#{commentEditor.new}"/>
     <h:outputText value="#{messages.Update}/#{messages.Delete} #{messages.Comment}" rendered="#{commentEditor.new}"/>
   </title>
   <style type="text/css" media="all">
    @import "style/default/screen.css";
   </style>
  </head>
  <body>
   <h:form>
   
     <h1>
         <h:outputText value="#{messages.Create} #{messages.Comment}" rendered="#{commentEditor.new}"/>
         <h:outputText value="#{messages.Update}/#{messages.Delete} #{messages.Comment}" rendered="#{!commentEditor.new}"/>
     </h1>
     
     <%@ include file="switcher.jsp" %> 
	
     <div class="rvgFind">
     <fieldset class="rvgFieldSet">
       <legend><h:outputText value="#{messages.Comment} #{messages.Attributes}"/></legend>
       
       <span class="rvgInputs">
         <span class="rvgMessage"><h:messages globalOnly="true"/></span>
         <h:outputLabel value="#{messages.Comment_text}" for="text">
           <h:inputTextarea value="#{commentEditor.instance.text}" id="text" rows="8"/>
           <span class="rvgMessage"><h:message for="text"/></span>
         </h:outputLabel>
         <h:outputLabel value="#{messages.Comment_submitted}" for="submitted">
           <h:inputText value="#{commentEditor.instance.submitted}" id="submitted" disabled="true">
               <f:convertDateTime type="both" dateStyle="short"/>
           </h:inputText>
           <span class="rvgMessage"><h:message for="submitted"/></span>
         </h:outputLabel>
       </span>

       <span class="rvgActions">
         <h:commandButton type="submit" value="#{messages.Create}" action="#{commentEditor.create}" rendered="#{commentEditor.new}"/>
         <h:commandButton type="submit" value="#{messages.Update}" action="#{commentEditor.update}" rendered="#{!commentEditor.new}"/>
         <h:commandButton type="submit" value="#{messages.Delete}" action="#{commentEditor.delete}" rendered="#{!commentEditor.new}"/>
         <h:commandButton type="submit" value="#{messages.Done}" action="#{commentEditor.done}"/>
       </span>
     
     </fieldset>
     </div>
    
        <div class="rvgResults">
           <h2><h:outputText value="#{messages.Comment_user}"/></h2>
           <h:dataTable var="parent" value="#{commentEditor.instance.user}" rowClasses="rvgRowOne,rvgRowTwo">
               <h:column>
                 <f:facet name="header"><h:outputText value="#{messages.User_username}"/></f:facet>
                 <h:outputText value="#{parent.username}"/>
               </h:column>
               <h:column>
                 <f:facet name="header"><h:outputText value="#{messages.User_name}"/></f:facet>
                 <h:outputText value="#{parent.name}"/>
               </h:column>
           </h:dataTable>
        </div>
        
        <div class="rvgResults">
           <h2><h:outputText value="#{messages.Comment_issue}"/></h2>
           <h:outputText value="#{messages.No} #{messages.Comment_issue}" rendered="#{commentEditor.instance.issue == null}"/>
           <h:dataTable var="parent" value="#{commentEditor.instance.issue}" 
                   rendered="#{commentEditor.instance.issue != null}"  rowClasses="rvgRowOne,rvgRowTwo">
               <h:column>
		         <f:facet name="header">
		           <h:outputText value="#{messages.Issue_user}"/>
		         </f:facet>
			     <h:outputText value="#{parent.user.username}"/>
			   </h:column>
               <h:column>
		         <f:facet name="header">
		           <h:outputText value="#{messages.Issue_project}"/>
		         </f:facet>
			     <h:outputText value="#{parent.project.name}"/>
			   </h:column>
               <h:column>
                 <f:facet name="header"><h:outputText value="#{messages.Issue_shortDescription}"/></f:facet>
                 <h:outputText value="#{parent.shortDescription}"/>
               </h:column>
               <h:column>
                 <f:facet name="header"><h:outputText value="#{messages.Issue_releaseVersion}"/></f:facet>
                 <h:outputText value="#{parent.releaseVersion}"/>
               </h:column>
               <h:column>
                 <f:facet name="header"><h:outputText value="#{messages.Issue_description}"/></f:facet>
                 <h:outputText value="#{parent.description}"/>
               </h:column>
           </h:dataTable>

        </div>
        
       
   </h:form>

  </body>
 </f:view>
</html>