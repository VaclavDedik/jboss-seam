<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
 <f:view>
 <f:loadBundle basename="messages" var="msg"/>
  <head>
   <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
   <title>
     <h:outputText value="#{msg.Register} #{msg.User}"/>
   </title>
   <style type="text/css" media="all">
    @import "style/default/screen.css";
   </style>
  </head>
  <body>
   <h:form>
   
     <h1>
         <h:outputText value="#{msg.Register} #{msg.User}"/>
     </h1>
	
     <div class="rvgFind">
     <fieldset class="rvgFieldSet">
       <legend><h:outputText value="#{msg.User} #{msg.Attributes}"/></legend>
       
       <span class="rvgInputs">
         <span class="rvgMessage"><h:messages globalOnly="true"/></span>
         <h:outputLabel value="#{msg.User_username}" for="username">
           <h:inputText value="#{userEditor.instance.username}" id="username"/>
           <span class="rvgMessage"><h:message for="username"/></span>
         </h:outputLabel>
         <h:outputLabel value="#{msg.User_name}" for="name">
           <h:inputText value="#{userEditor.instance.name}" id="name"/>
           <span class="rvgMessage"><h:message for="name"/></span>
         </h:outputLabel>
         <h:outputLabel value="#{msg.User_password}" for="password">
           <h:inputText value="#{userEditor.instance.password}" id="password"/>
           <span class="rvgMessage"><h:message for="password"/></span>
         </h:outputLabel>
       </span>

       <span class="rvgActions">
         <h:commandButton type="submit" value="#{msg.Register}" action="#{userEditor.create}"/>
       </span>
     
     </fieldset>
     </div>
       
   </h:form>

  </body>
 </f:view>
</html>