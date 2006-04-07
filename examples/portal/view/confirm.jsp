<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>

<div class="section">
	<h1>Confirm Hotel Booking</h1>
</div>
<div class="section">
	<h:form>
	<fieldset>
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
			<div class="label">Zip:</div>
			<div class="output"><h:outputText value="#{hotel.zip}"/></div>
		</div>
		<div class="entry">
			<div class="label">Check In Date:</div>
			<div class="output"><h:outputText value="#{booking.checkinDate}"><f:convertDateTime type="date"/></h:outputText></div>
		</div>
		<div class="entry">
			<div class="label">Check Out Date:</div>
			<div class="output"><h:outputText value="#{booking.checkoutDate}"><f:convertDateTime type="date"/></h:outputText></div>
		</div>
		<div class="entry">
			<div class="label">Credit Card #:</div>
			<div class="output"><h:outputText value="#{booking.creditCard}"/></div>
		</div>
		<div class="entry">
			<div class="label">&nbsp;</div>
			<div class="input">
				<h:commandButton value="Confirm" action="#{hotelBooking.confirm}" styleClass="button"/>&nbsp;
    			<h:commandButton value="Revise" action="back" styleClass="button"/>
			</div>
		</div>
	</fieldset>
	</h:form>
</div>

</f:view>