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
   
   <h:form>
   <h:dataTable value="#{hotelsDataModel}" var="hot">
    <h:column>
     <h:outputText value="#{hot.address}"/>
    </h:column>
    <h:column>
     <h:outputText value="#{hot.city}"/>
    </h:column>
    <h:column>
     <h:outputText value="#{hot.zip}"/>
    </h:column>
    <h:column>
    <h:commandLink action="#{findHotels.selectHotel}">
      <h:outputText value="View hotel"/>
    </h:commandLink>
    </h:column>
   </h:dataTable>
   </h:form>
   <br/>
    <h:commandLink action="#{logout.logout}">
      <h:outputText value="Logout"/>
    </h:commandLink>
  </f:view>
 </body>
</html>