<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>

<div class="section">
	<h1>Booking Confirmed</h1>
</div>
<div class="section">
	<h:outputText value="Thank you, #{user.name}, your confimation number is #{booking.id}."/>
</div>
<div class="section">
	<h:form>
	<fieldset class="buttonBox">
		<h:commandButton action="main" value="Done" styleClass="button"/>
	</fieldset>
	</h:form>
</div>

</f:view>