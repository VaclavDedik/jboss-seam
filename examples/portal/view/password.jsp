<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>

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
				<h:commandButton value="Change" action="#{changePassword.changePassword}" styleClass="button"/>&nbsp;
				<h:commandButton value="Cancel" action="#{changePassword.cancel}" styleClass="button"/>
			</div>
		</div>
	</fieldset>
	</h:form>
</div>

</f:view>