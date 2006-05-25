<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://jboss.com/products/seam/taglib" prefix="s" %>
<html>
 <head>
  <title>Register New User</title>
 </head>
 <body>
  <f:view>
   <h:form>
     <table border="0">
       <s:validateAll>
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
           <td><h:inputSecret value="#{user.password}"/></td>
         </tr>
       </s:validateAll>
     </table>
     <h:messages/>
     <h:commandButton type="submit" value="Register" action="#{register.register}"/>
   </h:form>
  </f:view>
 </body>
</html>