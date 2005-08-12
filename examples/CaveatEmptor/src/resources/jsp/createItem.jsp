<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Caveat Emptor</title>
 </head>
 <body>
  <h2>Create item</h2>
  <f:view>
   <h:form>
     <h:inputText value="#{item.name}"/>
     <h:inputText value="#{item.description}"/>
     <h:commandButton type="submit" value="Create Item" action="#{jsfItemManager.createItem}"/> 
   </h:form>
  </f:view>
 </body>
</html>