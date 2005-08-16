<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Edit User</title>
 </head>
 <body>
  <f:view>
   <h:form>
     <table border="0">
       <tr>
         <td>Username</td>
         <td><h:outputText value="#{updateUser.user.username}"/></td>
       </tr>
       <tr>
         <td>Real Name</td>
         <td><h:inputText value="#{updateUser.user.name}"/></td>
       </tr>
       <tr>
       <tr>
         <td>Password</td>
         <td><h:inputText value="#{updateUser.user.password}"/></td>
       </tr>
       <tr>
         <td>Age</td>
         <td><h:inputText value="#{updateUser.user.age}"/></td>
       </tr>
     </table>
     <h:commandButton type="submit" value="Change" action="#{updateUser.updateUser}"/>
   </h:form>
  </f:view>
 </body>
</html>