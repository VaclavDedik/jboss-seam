<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<html>

   <head>
      <title>Document Details</title>
   </head>

   <body>
      <f:view>
         <p>
            <h:commandLink action="main" value="Task List"/>
         </p>

         <hr/>

         <h:messages globalOnly="true" layout="list" errorClass="error"/>

         <h:form>
            <table width="100%">
               <tr>
                  <td>
                     Title:
                  </td>
                  <td>
                     <h:inputText id="title" value="#{document.title}" required="true" />
                     <f:verbatim>*</f:verbatim>
                     <h:message for="title" styleClass="error" />
                  </td>
               </tr>

               <tr>
                  <td colspan="2">
                     Content:
                  </td>
               </tr>
               <tr>
                  <td colspan="2">
                     <h:inputTextarea id="content" value="#{document.content}" required="true" />
                     <f:verbatim>*</f:verbatim>
                     <h:message for="content" styleClass="error" />
                  </td>
               </tr>

               <!-- display either the create button or the save/approve/reject button combo -->
               <h:panelGroup rendered="#{empty document.id}">
                  <tr>
                     <td colspan="2">
                        <h:commandButton action="#{documentCreator.create}" value="Create"/>
                     </td>
                  </tr>
               </h:panelGroup>

               <h:panelGroup rendered="#{!(empty document.id)}">
                  <tr>
                     <td colspan="2" align="center">
                        <h:commandButton action="#{documentEditor.save}" value="Save"/>
                        <h:commandButton action="#{documentEditor.approve}" value="Approve"/>
                        <h:commandButton action="#{documentEditor.reject}" value="Reject"/>
                     </td>
                  </tr>
               </h:panelGroup>

            </table>
         </h:form>

      </f:view>
   </body>

</html>