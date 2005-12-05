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
		<h1>Guided tour: Step 4</h1>

<p>As we already knew, the conversational state of this searching / booking operation is managed in the <code>HotelBookingAction</code> bean. So here, you can just <b>press the "Book Hotel" button</b>. The <code>HotelBookingAction</code> bean would create a new <code>Booking</code> object with the selected hotel and the current date. The <code>Booking</code> object is also managed in the conversation by the <code>HotelBookingAction</code> bean.</p>

<h1>Don't kill your database</h1>
<p>
    Keeping conversational state in memory in the middle tier is a great way to
    improve your application's scalability. It saves hitting the database every
    time we refresh a page, to re-read the data we were just looking at five 
    seconds ago. By using Seam's conversation context, we get a natural cache 
    of data associated with the what the user is currently doing. By nature, 
    this cache has a more efficient eviction policy than the MRU-type algorithms 
    used by a typical second-level data cache in an O/R mapping engine like
    Hibernate (at least for some kinds of data). Of course, you should use 
    a clever combination of second-level caching and conversational data
    caching to achieve the best performance for your application.
</p>
		</div>
		<div id="content">
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
		<h:commandButton action="#{hotelBooking.lastHotel}" value="Previous Hotel"/>&nbsp;
		<h:commandButton action="#{hotelBooking.nextHotel}" value="Next Hotel"/>&nbsp;
		<h:commandButton action="#{hotelBooking.bookHotel}" value="Book Hotel"/>&nbsp;
		<h:commandButton action="main" value="Back to Search"/>
	</fieldset>
	</h:form>
</div>
</div>
	</div>
	<div id="footer">Created with JBoss EJB 3.0, JBoss Seam and MyFaces</div>
</div>
</f:view>
