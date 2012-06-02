<rich:toolbar
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:ui="http://java.sun.com/jsf/facelets"
    xmlns:h="http://java.sun.com/jsf/html"
    xmlns:f="http://java.sun.com/jsf/core"
    xmlns:s="http://jboss.org/schema/seam/taglib"
    xmlns:rich="http://richfaces.org/rich">
    <rich:toolbarGroup>
        <h:outputText value="${'#'}{projectName}:"/>
        <s:link id="menuHomeId" view="/home.xhtml" value="Home" propagation="none"/>
    </rich:toolbarGroup>
    <rich:dropDownMenu showDelay="250" hideDelay="0" submitMode="none">
        <f:facet name="label">Browse data</f:facet>
<#foreach entity in c2j.getPOJOIterator(cfg.classMappings)>
	<rich:menuItem>
    	<s:link view="/${entity.shortName}List.xhtml"
           	value="${entity.shortName} List"
           	id="${entity.shortName}Id"
			includePageParams="false"
     		propagation="none"/>
     </rich:menuItem>
</#foreach>
    </rich:dropDownMenu>
    <!-- @newMenuItem@ -->
    <rich:toolbarGroup location="right">
        <h:outputText id="menuWelcomeId" value="signed in as: ${'#'}{credentials.username}" rendered="${'#'}{identity.loggedIn}"/>
        <s:link id="menuLoginId" view="/login.xhtml" value="Login" rendered="${'#'}{not identity.loggedIn}" propagation="none"/>
        <s:link id="menuLogoutId" view="/home.xhtml" action="${'#'}{identity.logout}" value="Logout" rendered="${'#'}{identity.loggedIn}" propagation="none"/>
    </rich:toolbarGroup>
</rich:toolbar>
