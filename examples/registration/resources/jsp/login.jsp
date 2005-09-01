<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Login</title>
 </head>
 <body>
  <f:view>
     <table border="0">
       <tr>
         <td>Username</td>
         <td><h:outputText value="#{user.username}"/></td>
       </tr>
       <tr>
         <td>Name</td>
         <td><h:outputText value="#{user.name}"/></td>
       </tr>
       <tr>
         <td>Password</td>
         <td><h:outputText value="#{user.password}"/></td>
       </tr>
     </table>
  </f:view>
 </body>
</html>