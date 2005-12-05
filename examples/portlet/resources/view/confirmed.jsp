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
		<h1>Guided tour: Done</h1>
<p>Now you've done it! You have finished the guided tour. Fell free to explore other features of the application. In the end of the day, SEAM provides the glue between business components and presentation components. It provides sophisticated yet easy-to-use state management for multiple page transactions. SEAM not only reduces the amount of code required for web applications, but also make the applications architecturally simpler and easier to maintain.</p>

<h1>Credits</h1>
<p>
    This most excellentest example application would not exist were it not for
    back-breaking physical effort of the following individuals and groups:
    <ul>
    <li>Jacob Hookom (design and presentation)</li>
    <li>Gavin King (code)</li>
    <li>Our mothers (having us)</li>
    </ul>
</p>
</div>
		<div id="content">
<div class="section">
	<h1>Booking Confirmed</h1>
</div>
<div class="section">
	Thank you, <h:outputText value="#{user.name}"/>, your confimation number is <h:outputText value="#{booking.id}"/>.
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
