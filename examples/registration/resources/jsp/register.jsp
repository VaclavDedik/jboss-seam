<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Registration</title>
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
         <td>Password</td>
         <td><h:inputText value="#{user.password}"/></td>
       </tr>
       <tr>
         <td>Age</td>
         <td><h:inputText converter="javax.faces.Integer" value="#{user.age}"/></td>
       </tr>
     </table>
     <h:commandButton type="submit" value="Register" action="#{userManagement.register}"/>
   </h:form>
  </f:view>
 </body>
</html>