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
<h1>Flexible navigation</h1>
<p>
    You can do other things while you're in a conversation, and then come back
    to it, either be clicking links, or by back button. Your conversation will
    be there waiting for you. This works whether your conversation state is
    being held on the server, or in the browser.
</p>
</div>
		<div id="content">	
<div class="section">
	<h1>Current Hotel Bookings</h1>
</div>
<div class="section">
	<h:outputText value="No Bookings Found" rendered="#{bookings.rowCount==0}"/>
	<h:dataTable value="#{bookings}" var="book" rendered="#{bookings.rowCount>0}">
		<h:column>
			<f:facet name="header">Name</f:facet>
			<h:outputText value="#{book.hotel.name}"/>
		</h:column>
		<h:column>
			<f:facet name="header">Address</f:facet>
			<h:outputText value="#{book.hotel.address}"/>
		</h:column>
		<h:column>
			<f:facet name="header">City, State</f:facet>
			<h:outputText value="#{book.hotel.city}, #{book.hotel.state}"/>
		</h:column>
		<h:column>
			<f:facet name="header">Check in date</f:facet>
			<h:outputText value="#{book.checkinDate}"/>
		</h:column>
		<h:column>
			<f:facet name="header">Check out date</f:facet>
			<h:outputText value="#{book.checkoutDate}"/>
		</h:column>
		<h:column>
			<f:facet name="header">Confirmation number</f:facet>
			<h:outputText value="#{book.id}"/>
		</h:column>
		<h:column>
			<f:facet name="header">Action</f:facet>
			<h:commandLink action="#{bookingList.cancel}"><h:outputText value="Cancel"/></h:commandLink>
		</h:column>
	</h:dataTable>
</div>
<div class="section">
	<h:form>
	<fieldset class="buttonBox">
		<h:commandButton action="main" value="Done"/>
	</fieldset>
	</h:form>
</div>
</div>
	</div>
	<div id="footer">Created with JBoss EJB 3.0, JBoss Seam and MyFaces</div>
</div>
</f:view>

