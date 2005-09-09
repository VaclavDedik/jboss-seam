<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Change Password</title>
 </head>
 <body>
  <f:view>
   <h:form>
     <table border="0">
       <tr>
         <td>Username</td>
         <td><h:outputText value="#{user.username}"/></td>
       </tr>
       <tr>
         <td>Password</td>
         <td><h:inputSecret value="#{user.password}"/></td>
       </tr>
       <tr>
         <td>Verify</td>
         <td><h:inputSecret value="#{changePassword.verify}"/></td>
       </tr>
     </table>
     <h:commandButton type="submit" value="Change" action="#{changePassword.changePassword}"/>
     <h:commandButton type="submit" value="Cancel" action="main"/>
   </h:form>
  </f:view>
 </body>
</html>