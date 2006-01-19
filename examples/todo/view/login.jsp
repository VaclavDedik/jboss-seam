<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<html>
<head>
<title>Login</title>
</head>
<body>
<h1>Login</h1>
<f:view>
	<h:form>
	  <div>
	    <h:inputText value="#{login.user}"/>
	    <h:commandButton value="Login" action="#{login.login}"/>
	  </div>
	</h:form>
</f:view>
</body>
</html>
