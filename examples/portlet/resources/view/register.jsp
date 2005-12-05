<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>

<f:view>
<h:form>
<div id="document">
	<div id="seamheader">
		<div id="title"><img src="img/hdr.title.gif" /></div>
	</div>
	<div id="container">
		<div id="sidebar">
		    <h1>Guided tour: Step 2</h1>
		    <p>Now, <b>fill in the form</b> and register an account for yourself. SEAM maps the form data to an EJB 3.0 entity bean and the form action to an EJB 3.0 session bean. Click the link below to see the behind-the-scene code snippets and explanations.</p>
		    
		    <p><b><a href="#" onClick="window.open('registerExp.html','exp','width=752,height=500,scrollbars=yes');">See under the hood</a></b></p>
		    
			<h1>Integrated multi-layer validation</h1>
			<p>
			    Robust applications need data validation in several different places. Seam integrates Hibernate Validator,
			    a set of annotations for expressing data model constraints in your domain model classes. Then, these 
			    constraints are applied almost completely transparently at three levels of the application: by Seam when 
			    the user first enters data, by EJB before persisting data to the database, and, if you use Hibernate to 
			    generate your database schema, by the database constraints themselves. Multi-layer validation hardens
			    your application and protects your data. Even better, it's self-documenting, and easy to change when
			    your business rules change.
			</p>
		</div>
		<div id="content">
			<div class="section">
				<h1>Register</h1>
			</div>
			<div class="section">
				<fieldset>
					<div class="entry">
						<div class="label"><h:outputLabel for="username">Username:</h:outputLabel></div>
						<div class="input"><h:inputText id="username" value="#{user.username}"/><br/><span class="errors"><h:message for="username" /></span></div>
					</div>
					<div class="entry">
						<div class="label"><h:outputLabel for="name">Real Name:</h:outputLabel></div>
						<div class="input"><h:inputText id="name" value="#{user.name}" /><br/><span class="errors"><h:message for="name" /></span></div>
					</div>
					<div class="entry">
						<div class="label"><h:outputLabel for="password">Password:</h:outputLabel></div>
						<div class="input"><h:inputSecret id="password" value="#{user.password}" /><br/><span class="errors"><h:message for="password" /></span></div>
					</div>
					<div class="entry">
						<div class="label"><h:outputLabel for="verify">Verify Password:</h:outputLabel></div>
						<div class="input"><h:inputSecret id="verify" value="#{register.verify}" /><br/><span class="errors"><h:message for="verify" /></span></div>
					</div>
					<div class="entry errors"><h:messages globalOnly="true" /></div>
					<div class="entry">
						<div class="label">&nbsp;</div>
						<div class="input">
							<h:commandButton value="Register" action="#{register.register}" />&nbsp;
							<h:commandButton value="Cancel" action="login"/>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
	</div>
	<div id="footer">Created with JBoss EJB, JBoss Seam and MyFaces</div>
</div>
</h:form>
</f:view>

