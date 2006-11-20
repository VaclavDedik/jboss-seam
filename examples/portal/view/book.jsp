<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://jboss.com/products/seam/taglib" prefix="s" %>
<f:view>

<div class="section">
	<h1>Book Hotel</h1>
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
		<s:validateAll>
		<div class="entry">
			<div class="label"><h:outputLabel for="checkinDate">Check In Date:</h:outputLabel></div>
			<div class="input"><h:inputText id="checkinDate" value="#{booking.checkinDate}"><s:convertDateTime type="date"/></h:inputText><br/><span class="errors"><h:message for="checkinDate" /></span></div>
		</div>
		<div class="entry">
			<div class="label"><h:outputLabel for="checkoutDate">Check Out Date:</h:outputLabel></div>
			<div class="input"><h:inputText id="checkoutDate" value="#{booking.checkoutDate}"><s:convertDateTime type="date"/></h:inputText><br/><span class="errors"><h:message for="checkoutDate" /></span></div>
		</div>
		<div class="entry">
			<div class="label"><h:outputLabel for="creditCard">Credit Card #:</h:outputLabel></div>
			<div class="input"><h:inputText id="creditCard" value="#{booking.creditCard}" /><br/><span class="errors"><h:message for="creditCard" /></span></div>
		</div>
		<div class="entry errors"><h:messages globalOnly="true" /></div>
		<div class="entry">
			<div class="label">&nbsp;</div>
			<div class="input">
				<h:commandButton value="Proceed" action="#{hotelBooking.setBookingDetails}" styleClass="button"/>&nbsp;
				<h:commandButton value="Back To Search" action="main" styleClass="button"/>
			</div>
		</div>
		</s:validateAll>
	</fieldset>
	</h:form>
</div>

</f:view>