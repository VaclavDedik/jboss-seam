<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>
    <h:form>
			<fieldset>
				<div>
					<h:outputLabel for="username" value="Login Name"/>
					<h:inputText id="username" value="#{identity.username}" style="width: 175px;"/>
				</div>
				<div>
					<h:outputLabel for="password" value="Password"/>
					<h:inputSecret id="password" value="#{identity.password}" style="width: 175px;"/>
				</div>
				<div class="errors"><h:messages globalOnly="true"/></div>
				<div class="buttonBox"><h:commandButton action="#{identity.login}" value="Account Login" styleClass="button" /></div>
				<div class="notes"><h:commandLink action="register" value="Register User"/></div>
			</fieldset>
    </h:form>
</f:view>
