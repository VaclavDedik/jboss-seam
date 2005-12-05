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
		<h1>Guided tour: Step 6</h1>
<p>To complete the guided tour, <b>click on the "Confirm" button</b>. The <code>HotelBookingAction</code> bean would save the booking into the database and finish the conversation. If you click on the "Cancel" or "Back" button, the conversation state would not end and you will be able to continue work on your hotel list. Click the link below to see detailed explanations with code under the hood.</p>

<p><b><a href="#" onClick="window.open('confirmExp.html','exp','width=752,height=500,scrollbars=yes');">See under the hood</a></b></p>
		</div>
		<div id="content">
<div class="section">
	<h1>Confirm Hotel Booking</h1>
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
				<h:commandButton value="Confirm" action="#{hotelBooking.confirm}"/>&nbsp;
    			<h:commandButton value="Cancel" action="main"/>&nbsp;
    			<h:commandButton value="Back" action="back"/>
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
