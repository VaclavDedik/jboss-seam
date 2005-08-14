<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Set User Password</title>
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
       <tr>
         <td>Password</td>
         <td><h:inputText value="#{user.password}"/></td>
       </tr>
     </table>
     <h:commandButton type="submit" value="Change" action="#{userManagement.setPassword}"/>
   </h:form>
  </f:view>
 </body>
</html>