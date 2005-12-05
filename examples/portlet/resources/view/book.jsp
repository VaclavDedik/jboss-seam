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
		<h1>Guided tour: Step 5</h1>
<p>Now, <b>fill in a checkout date</b> that is later than the check in date, and <b>fill in a 16 digit credit card number</b>. For example, you can use 0123456789123456 as the credit card number. <b>Click on the Proceed button</b> to finish booking.</p>

<p>The action method for the Proceed button validates the checkout date and credit card number. If those numbers are invalid, this page will be re-displayed with the error message. We have discussed validation in the registration page.</p>
		</div>
		<div id="content">
<div class="section">
	<h1>Book Hotel</h1>
</div>
<div class="section">
	<h:form>
	<fieldset>
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
			<div class="label"><h:outputLabel for="checkinDate">Check In Date:</h:outputLabel></div>
			<div class="input"><h:inputText id="checkinDate" value="#{booking.checkinDate}"><f:convertDateTime type="date"/></h:inputText><br/><span class="errors"><h:message for="checkinDate" /></span></div>
		</div>
		<div class="entry">
			<div class="label"><h:outputLabel for="checkoutDate">Check Out Date:</h:outputLabel></div>
			<div class="input"><h:inputText id="checkoutDate" value="#{booking.checkoutDate}"><f:convertDateTime type="date"/></h:inputText><br/><span class="errors"><h:message for="checkoutDate" /></span></div>
		</div>
		<div class="entry">
			<div class="label"><h:outputLabel for="creditCard">Credit Card #:</h:outputLabel></div>
			<div class="input"><h:inputText id="creditCard" value="#{booking.creditCard}" /><br/><span class="errors"><h:message for="creditCard" /></span></div>
		</div>
		<div class="entry errors"><h:messages globalOnly="true" /></div>
		<div class="entry">
			<div class="label">&nbsp;</div>
			<div class="input">
				<h:commandButton value="Proceed" action="#{hotelBooking.setBookingDetails}"/>&nbsp;
				<h:commandButton value="Cancel" action="main"/>
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

