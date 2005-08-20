<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Login</title>
 </head>
 <body>
  <f:view>
   <h:form>
     <table border="0">
       <tr>
         <td>Address</td>
         <td><h:outputText value="#{hotel.address}"/></td>
       </tr>
       <tr>
         <td>City</td>
         <td><h:outputText value="#{hotel.city}"/></td>
       </tr>
       <tr>
         <td>Zip</td>
         <td><h:outputText value="#{hotel.zip}"/></td>
       </tr>
     </table>
    <h:commandLink action="#{findHotels.lastHotel}">
      <h:outputText value="View previous hotel"/>
    </h:commandLink>
    <h:commandLink action="#{findHotels.nextHotel}">
      <h:outputText value="View next hotel"/>
    </h:commandLink>
    <h:commandLink action="back">
      <h:outputText value="Back to list"/>
    </h:commandLink>
   </h:form>
  </f:view>
 </body>
</html>