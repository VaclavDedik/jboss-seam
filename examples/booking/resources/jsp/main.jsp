<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Login</title>
 </head>
 <body>
  <f:view>
   <p>
    Welcome, <h:outputText value="#{user.name}"/>
   </p>
   <h:form>
   <p>
    Search for a hotel: 
    <h:inputText value="#{findHotels.searchString}"/>
    <h:commandButton type="submit" value="Find" action="#{findHotels.find}"/>
   </p>
   </h:form>
   <h:dataTable value="#{findHotels.hotels}" var="hotel">
    <h:column>
     <h:outputText value="#{hotel.address}"/>
    </h:column>
    <h:column>
     <h:outputText value="#{hotel.city}"/>
    </h:column>
    <h:column>
     <h:outputText value="#{hotel.zip}"/>
    </h:column>
   </h:dataTable>
  </f:view>
 </body>
</html>