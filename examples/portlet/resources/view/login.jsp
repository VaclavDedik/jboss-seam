<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>

<f:view>
<h:form>
<div id="document">
	<div id="seamheader">
		<div id="title"><img src="img/hdr.title.gif" /></div>
	</div>
	<div id="container">
		<div id="sidebar">
			<fieldset>
				<div>
					<h:outputLabel for="username">Login Name</h:outputLabel>
					<h:inputText id="username" value="#{user.username}" style="width: 175px;" />
				</div>
				<div>
					<h:outputLabel for="password">Password</h:outputLabel>
					<h:inputSecret id="password" value="#{user.password}" style="width: 175px;" />
				</div>
				<div class="errors"><h:messages globalOnly="true"/></div>
				<div class="buttonBox"><h:commandButton action="#{login.login}" value="Account Login"/></div>
				<div class="notes"><h:commandLink action="register"><h:outputText value="Register User"/></h:commandLink> | Ask for Help</div>
			</fieldset>
		</div>
		<div id="content">
			<div class="section">
				<h1>Guided tour: Step 3</h1>
				<p>Now you probably have returned to this page from the registration page. Notice that the username field here is already populated with the username you chose in the registration page. The reason is that this field is backed by the same <code>user.username</code> bean property as the username field in the registration page. Since the <code>User</code> entity bean is managed in the session scope by SEAM. This value persists across pages. Now, <b>enter your password and click login</b> to log into the application. Click the link below to see detailed explanations with code under the hood.</p>
				
				<p><b><a href="#" onClick="window.open('loginExp.html','exp','width=752,height=500,scrollbars=yes');">See under the hood</a></b></p>
								
			</div>
		</div>
	</div>
	<div id="footer">Created with JBoss EJB 3.0, JBoss Seam and MyFaces</div>
</div>
</h:form>
</f:view>

