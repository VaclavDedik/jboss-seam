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
       <tr>
         <td>Check In Date</td>
         <td><h:inputText value="#{booking.checkinDate}"/></td>
       </tr>
       <tr>
         <td>Check Out Date</td>
         <td><h:inputText value="#{booking.checkoutDate}"/></td>
       </tr>
     </table>
    <h:commandButton type="submit" value="Confirm" action="#{findHotels.confirm}"/>
   </h:form>
   <br/>
    <h:commandLink action="#{logout.logout}">
      <h:outputText value="Logout"/>
    </h:commandLink>
    <h:commandLink action="password">
      <h:outputText value="Change password"/>
    </h:commandLink>
  </f:view>
 </body>
</html>