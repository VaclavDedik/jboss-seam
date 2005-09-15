<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<html>

   <head>
      <title>Document Tasks</title>
   </head>

   <body>
      <f:view>
         <p>
            <h:commandLink action="create" value="New Document"/>
         </p>

         <hr/>

         <h:form>
            <h:dataTable value="#{jbpmUtil.taskLists[ user.username ]}" var="task">
               <h:column>
                  <h:commandLink action="#{documentViewer.details}" value="#{task.taskMgmtInstance.processInstance.contextInstance.variables['description']}">
                     <f:param name="jbpmTaskId" value="#{task.id}"/>
                  </h:commandLink>
               </h:column>
            </h:dataTable>
         </h:form>
      </f:view>
   </body>

</html>
