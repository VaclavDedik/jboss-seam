<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Book Hotel</title>
 </head>
 <body>
  <f:view>
   <h:form>
     <h:messages/>
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
         <td><h:inputText id="checkinDate" value="#{booking.checkinDate}"/><h:message for="checkinDate"/></td>
       </tr>
       <tr>
         <td>Check Out Date</td>
         <td><h:inputText id="checkoutDate" value="#{booking.checkoutDate}"/><h:message for="checkinDate"/></td>
       </tr>
       <tr>
         <td>Credit Card Number</td>
         <td><h:inputText id="creditCard" value="#{booking.creditCard}"/><h:message for="creditCard"/></td>
       </tr>
     </table>
    <h:commandButton type="submit" value="Proceed" action="#{hotelBooking.setBookingDetails}"/>
    <h:commandButton type="submit" value="Cancel" action="main"/>
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