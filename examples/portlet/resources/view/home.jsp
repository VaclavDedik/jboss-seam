<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>

<f:view>
<div id="document">
	<div id="seamheader">
		<div id="title"><img src="img/hdr.title.gif" /></div>
	</div>
	<div id="container">
		<div id="sidebar">
			<fieldset>
<h:form>
				<div>
					<h:outputLabel for="username">Login Name</h:outputLabel>
					<h:inputText id="username" value="#{user.username}" style="width: 175px;" />
				</div>
				<div>
					<h:outputLabel for="password">Password</h:outputLabel>
					<h:inputSecret id="password" value="#{user.password}" style="width: 175px;" />
				</div>
				<div class="errors"><h:messages globalOnly="true"/></div>
				<div class="buttonBox"><h:commandButton action="#{login.login}" value="Account Login" /></div>
				<div class="notes"><h:commandLink action="register"><h:outputText value="Register User"/></h:commandLink> | Ask for Help</div>
</h:form>
			</fieldset>
		</div>
		<div id="content">
		    <div class="section">
				<h1>Take the guided tour</h1>
				<p>This example application is designed to showcase how easy it is to develop database-driven enterprise web applications with JBoss SEAM, EJB 3.0, and Facelets. Since SEAM is the glue that ties the application, it will be the focus of this example. Throughout the application, you will see step-by-step navigation tips and links to the behind-the-scene code snippets on the left sidebar of the page. Just follow those instructions and you will learn how to use SEAM to assemble a working web application in minutes.</p>
				
				<b>Guide Tour: Step 1</b><br/>
				<p>So, as the first step in the guided tour, <b>click on the "register" link</b> to the left and it will bring you the registration form. You need to register an account to use the application.</p>
			</div>
			
			<div class="section">
				<h1>About this example application</h1>
				
				<p>Compared with traditional web development technologies, the EJB 3.0 + SEAM + Facelets approach only requires a fraction of the code, needs very little XML configuration files, and eliminates a whole class of bugs. In technical terms, the SEAM + EJB 3.0 + Facelets stack allows us to write the entire application using plain old Java objects (POJOs) and simple XHTML. There is no more boiler plate code and no more trivial multi-layer abstraction / delegation.</p>
				
				<ul>
				    <li>EJB 3.0 allows us to use simple POJOs to model the database (entity bean) and develop the business logic (session bean).</li>
				    
				    <li>Facelets allows us to author web pages in plain XHTML while taking advantage of JSF UI and component model.</li>
				
				    <li>JBoss SEAM glues EJB 3.0 POJOs with JSF UI components and event handlers. It automatically manages the application's conversational state and eliminates the needs for JSF backing beans.</li>
				</ul>
				
				<p>Just like all JBoss software products, this entire software stack is free. Now, let's take a guided tour through the example application to learn how JBoss SEAM ties the application together.</p>
			</div>
			
		</div>
	</div>
	<div id="footer">Created with JBoss EJB 3.0, JBoss Seam and MyFaces</div>
</div>
</f:view>

