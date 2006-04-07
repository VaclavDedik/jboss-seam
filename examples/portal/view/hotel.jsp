<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>

<div class="section">
	<h1>View Hotel</h1>
</div>
<div class="section">
	<div class="entry">
		<div class="label">Name:</div>
		<div class="output"><h:outputText value="#{hotel.name}"/></div>
	</div>
	<div class="entry">
		<div class="label">Address:</div>
		<div class="output"><h:outputText value="#{hotel.address}"/></div>
	</div>
	<div class="entry">
		<div class="label">City:</div>
		<div class="output"><h:outputText value="#{hotel.city}"/></div>
	</div>
	<div class="entry">
		<div class="label">State:</div>
		<div class="output"><h:outputText value="#{hotel.state}"/></div>
	</div>
	<div class="entry">
		<div class="label">Zip:</div>
		<div class="output"><h:outputText value="#{hotel.zip}"/></div>
	</div>
</div>

<div class="section">
	<h:form>
	<fieldset class="buttonBox">
		<h:commandButton action="#{hotelBooking.lastHotel}" value="Previous Hotel" styleClass="button"/>&nbsp;
		<h:commandButton action="#{hotelBooking.nextHotel}" value="Next Hotel" styleClass="button"/>&nbsp;
		<h:commandButton action="#{hotelBooking.bookHotel}" value="Book Hotel" styleClass="button"/>&nbsp;
		<h:commandButton action="main" value="Back to Search" styleClass="button"/>
	</fieldset>
	</h:form>
</div>

</f:view>