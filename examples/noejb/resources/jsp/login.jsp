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
         <td>Username</td>
         <td><h:inputText value="#{user.username}"/></td>
       </tr>
       <tr>
         <td>Password</td>
         <td><h:inputSecret value="#{user.password}"/></td>
       </tr>
     </table>
     <h:commandButton type="submit" value="Login" action="#{login.login}"/>
   </h:form>
   <h:commandLink action="register">
     <h:outputText value="Or, register now"/>
   </h:commandLink>
  </f:view>
 </body>
</html>