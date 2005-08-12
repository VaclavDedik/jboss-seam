<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Users</title>
 </head>
 <body> 
  <h2>Registered:</h2>
  <f:view>
   <h:form>
     <table border="0">
       <tr>
         <td>Username</td>
         <td><h:outputText value="#{user.username}"/></td>
       </tr>
       <tr>
         <td>Password</td>
         <td><h:outputText value="#{user.password}"/></td>
       </tr>
       <tr>
         <td>Age</td>
         <td><h:outputText converter="javax.faces.Integer" value="#{user.age}"/></td>
       </tr>
     </table>
   </h:form>
  </f:view>
 </body>
</html>