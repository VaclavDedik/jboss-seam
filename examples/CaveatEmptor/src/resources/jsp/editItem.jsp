<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Caveat Emptor</title>
 </head>
 <body>
  <h2>Edit item</h2>
  <f:view>
   <h:form>
     <h:outputText value="#{jsfItemManager.item.name}"/>
     <h:inputText value="#{jsfItemManager.item.description}"/>
     <h:inputHidden converter="javax.faces.Long" value="#{jsfItemManager.processInstanceId}"/>
     
     <h:commandButton type="submit" value="Save Item" action="#{jsfItemManager.saveItem}"/> 
   </h:form>
  </f:view>
 </body>
</html>