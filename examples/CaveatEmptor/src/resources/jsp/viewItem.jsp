<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Caveat Emptor</title>
 </head>
 <body>
  <h2>Your item</h2>
  <f:view>
   <h:form>
     <h:outputText value="#{jsfItemManager.item.name}"/>
     <h:outputText value="#{jsfItemManager.item.description}"/>

     <h:commandLink value="Edit Item" action="#{jsfItemManager.editItem}">
		<f:param name="processInstanceId" value="#{jsfItemManager.processInstanceId}"/>
     </h:commandLink>
     <h:commandLink value="Submit Item" action="#{jsfItemManager.submitItem}">
		<f:param name="processInstanceId" value="#{jsfItemManager.processInstanceId}"/>
     </h:commandLink>
   </h:form>
  </f:view>
 </body>
</html>