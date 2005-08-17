<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Display New User</title>
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
         <td>Real Name</td>
         <td><h:outputText value="#{user.name}"/></td>
       </tr>
       <tr>
         <td>Password</td>
         <td><h:outputText value="#{user.password}"/></td>
       </tr>
       <tr>
         <td>Age</td>
         <td><h:outputText value="#{user.age}"/></td>
       </tr>
     </table>
     <!-- <h:outputText value="#{user.version}"/> -->
   </h:form>
  </f:view>
 </body>
</html>