<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Messages</title>
 </head>
 <body>
  <f:view>
   <h:form>
     <h2>Message List</h2>
     <h:outputText value="No messages to display" rendered="#{messages.rowCount==0}"/>
     <h:dataTable var="msg" value="#{messages}" rendered="#{messages.rowCount>0}">
        <h:column>
           <f:facet name="header">
              <h:outputText value="Read"/>
           </f:facet>
           <h:selectBooleanCheckbox value="#{msg.read}" disabled="true"/>
        </h:column>
        <h:column>
           <f:facet name="header">
              <h:outputText value="Title"/>
           </f:facet>
           <h:commandLink value="#{msg.title}" action="#{messageList.select}"/>
        </h:column>
        <h:column>
           <f:facet name="header">
              <h:outputText value="Date/Time"/>
           </f:facet>
           <h:outputText value="#{msg.datetime}">
              <f:convertDateTime type="both" dateStyle="medium" timeStyle="short"/>
           </h:outputText>
        </h:column>
        <h:column>
           <h:commandButton value="Delete" action="#{messageList.delete}"/>
        </h:column>
     </h:dataTable>
     <h3><h:outputText value="#{message.title}"/></h3>
     <div><h:outputText value="#{message.text}"/></div>
   </h:form>
  </f:view>
 </body>
</html>