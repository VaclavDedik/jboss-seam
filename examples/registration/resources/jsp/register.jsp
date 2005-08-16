<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Create New User</title>
 </head>
 <body>
  <f:view>
   <h:form>
     <table border="0">
       <tr>
         <td>Username</td>
         <td><h:inputText value="#{user.username}"/></td>
       </tr>
       <tr>
         <td>Real Name</td>
         <td><h:inputText value="#{user.name}"/></td>
       </tr>
       <tr>
         <td>Password</td>
         <td><h:inputText value="#{user.password}"/></td>
       </tr>
       <tr>
         <td>Age</td>
         <td><h:inputText value="#{user.age}"/></td>
       </tr>
     </table>
     <h:commandButton type="submit" value="Register" action="#{createUser.createUser}"/>
   </h:form>
  </f:view>
 </body>
</html>