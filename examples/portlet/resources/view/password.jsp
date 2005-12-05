<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>

<f:view>
<div id="document">
	<div id="seamheader">
		<div id="title"><img src="img/hdr.title.gif" /></div>
		<div id="status">
			Welcome <h:outputText value="#{user.name}"/>
			| <h:commandLink action="password"><h:outputText value="Settings"/></h:commandLink>
			| <h:commandLink action="#{logout.logout}"><h:outputText value="Logout"/></h:commandLink>
		</div>
	</div>
	<div id="container">
		<div id="sidebar">
		<h1>Simple things should be easy</h1>
<p>
    (And so should some complex things.) You shouldn't have to write four different classes
    just to change a password. Traditional J2EE architectures require that developers spend
    more time writing code to make the frameworks happy, than they ever get to spend writing
    code to make the user happy. Seam lets you reduce the size of your code dramatically.
    And that reduces bugs. And it makes refactoring easier. And it makes delivering new 
    functionality quicker. Productivity matters. But with Seam, JSF, EJB 3.0 and jBPM, you 
    don't need to sacrifice the ability to handle complex problems just to achieve great
    productivity.
</p>
</div>
		<div id="content">
<div class="section">
	<h1>Change Your Password</h1>
</div>
<div class="section">
	<h:form>
	<fieldset>
		<div class="entry">
			<div class="label"><h:outputLabel for="password">Password:</h:outputLabel></div>
			<div class="input"><h:inputSecret id="password" value="#{user.password}" /><br/><span class="errors"><h:message for="password" /></span></div>
		</div>
		<div class="entry">
			<div class="label"><h:outputLabel for="password">Verify:</h:outputLabel></div>
			<div class="input"><h:inputSecret id="verify" value="#{changePassword.verify}" /><br/><span class="errors"><h:message for="verify" /></span></div>
		</div>
		<div class="entry errors"><h:messages globalOnly="true"/></div>
		<div class="entry">
			<div class="label">&nbsp;</div>
			<div class="input">
				<h:commandButton value="Change" action="#{changePassword.changePassword}"/>&nbsp;
				<h:commandButton value="Cancel" action="#{changePassword.cancel}"/>
			</div>
		</div>
	</fieldset>
	</h:form>
</div>
		</div>
	</div>
	<div id="footer">Created with JBoss EJB 3.0, JBoss Seam and MyFaces</div>
</div>
</f:view>
