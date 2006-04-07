<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>
    <h:form>
			<div class="section">
				<h1>Register</h1>
			</div>
			<div class="section">
				<fieldset>
					<div class="entry">
						<div class="label"><h:outputLabel for="username">Username:</h:outputLabel></div>
						<div class="input"><h:inputText id="username" value="#{user.username}"/><br/><span class="errors"><h:message for="username" /></span></div>
					</div>
					<div class="entry">
						<div class="label"><h:outputLabel for="name">Real Name:</h:outputLabel></div>
						<div class="input"><h:inputText id="name" value="#{user.name}" /><br/><span class="errors"><h:message for="name" /></span></div>
					</div>
					<div class="entry">
						<div class="label"><h:outputLabel for="password">Password:</h:outputLabel></div>
						<div class="input"><h:inputSecret id="password" value="#{user.password}" /><br/><span class="errors"><h:message for="password" /></span></div>
					</div>
					<div class="entry">
						<div class="label"><h:outputLabel for="verify">Verify Password:</h:outputLabel></div>
						<div class="input"><h:inputSecret id="verify" value="#{register.verify}" /><br/><span class="errors"><h:message for="verify" /></span></div>
					</div>
					<div class="entry errors"><h:messages globalOnly="true" /></div>
					<div class="entry">
						<div class="label">&nbsp;</div>
						<div class="input">
							<h:commandButton value="Register" action="#{register.register}" styleClass="button"/>&nbsp;
							<h:commandButton value="Cancel" action="login" styleClass="button"/>
						</div>
					</div>
				</fieldset>
			</div>

    </h:form>
</f:view>