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
<p>Now, let's search for some hotels to book. <b>Put search string "atlanta" in the box and click on "Find Hotels"</b>. The page will be re-displayed with a list of Atlanta hotels. <b>Click on the "View Hotel" link</b> next to any of the hotels to view the hotel information. This page and the following several pages demonstrate a key feature of SEAM: the ability to manage multi-page conversations. Click the link below to see detailed explanations with code under the hood.</p>
<p><b><a href="#" onClick="window.open('mainExp.html','exp','width=752,height=500,scrollbars=yes');">See under the hood</a></b></p>
<h1>Contextual components</h1>
<p>In this page, we introduce a very important and very useful SEAM concept: contextual components. When you click "Find Hotels", a conversation begins. The state associated with that conversation is associated with this tab, in this browser window (contextual!). To see this point, try <a target="_blank" href="main.jsf">opening this page</a> in a new browser tab or window. Now, <b>search for "NY"</b> in that new browser window and complete the rest of the guided tour in both browser windows. As you can see, the work you do in each window will be completely isolated from the other one -- you can move back and forth with the "atlanta" and "NY" bookings as if each window is a separate application. In reality, the backing bean and page source for the two windows are identical. How's that possible? Well, click on the link below to see more.</p>
<p><b><a href="#" onClick="window.open('main2Exp.html','exp','width=752,height=500,scrollbars=yes');">See under the hood</a></b></p>
		
		</div>
		<div id="content">		
<div class="section">
	<h1>Search Hotels</h1>
	<h:form>
	<fieldset> 
		<h:inputText value="#{hotelBooking.searchString}" style="width: 165px;" />&nbsp;
		<h:commandButton value="Find Hotels" action="#{hotelBooking.find}" styleClass="button" />
	</fieldset>
	</h:form>
</div>
<div class="section">
	<h:outputText value="No Hotels Found" rendered="#{hotels != null and hotels.rowCount==0}"/>
	<h:dataTable value="#{hotels}" var="hot" rendered="#{hotels.rowCount>0}">
		<h:column>
			<f:facet name="header">Name</f:facet>
			<h:outputText value="#{hot.name}"/>
		</h:column>
		<h:column>
			<f:facet name="header">Address</f:facet>
			<h:outputText value="#{hot.address}"/>
		</h:column>
		<h:column>
			<f:facet name="header">City, State</f:facet>
			<h:outputText value="#{hot.city}, #{hot.state}"/>
		</h:column> 
		<h:column>
			<f:facet name="header">Zip</f:facet>
			<h:outputText value="#{hot.zip}"/>
		</h:column>
		<h:column>
			<f:facet name="header">Action</f:facet>
			<h:commandLink action="#{hotelBooking.selectHotel}"><h:outputText value="View Hotel"/></h:commandLink>
		</h:column>
	</h:dataTable>
</div>
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
</div>
	</div>
	<div id="footer">Created with JBoss EJB 3.0, JBoss Seam and MyFaces</div>
</div>
</f:view>


