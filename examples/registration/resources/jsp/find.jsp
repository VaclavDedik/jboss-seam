<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Find User</title>
 </head>
 <body>
  <f:view>
   <h:form>
     <table border="0">
       <tr>
         <td>Username</td>
         <td><h:inputText value="#{updateUser.username}"/></td>
       </tr>
     </table>
     <h:commandButton type="submit" value="Find" action="#{updateUser.findUser}"/>
   </h:form>
  </f:view>
 </body>
</html>