<div class="menuButtons" 
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:ui="http://java.sun.com/jsf/facelets"
		xmlns:h="http://java.sun.com/jsf/html"
		xmlns:f="http://java.sun.com/jsf/core"
		xmlns:ice="http://www.icesoft.com/icefaces/component"  
		xmlns:s="http://jboss.com/products/seam/taglib">
            <ice:panelGrid columns="2" columnClasses="leftMenu,rightMenu">
                <ice:panelGroup >
                            <ice:outputText value="${'#'}{projectName}:"/>
                            <s:link view="/home.xhtml" value="Home"/>
<#foreach entity in c2j.getPOJOIterator(cfg.classMappings)>
				    <s:link view="/${entity.shortName}List.xhtml" 
	       				value="${entity.shortName} List" 
	 					propagation="none"/>
</#foreach>         
		    </ice:panelGroup>
                <ice:panelGroup>
                                <h:outputText value="Welcome,${'#'}{identity.username}" rendered="${'#'}{identity.loggedIn}"/>
                                <s:link view="/login.xhtml" value="Login" rendered="${'#'}{not identity.loggedIn}"/>
                                <s:link view="/home.xhtml" action="${'#'}{identity.logout}" value="Logout" rendered="${'#'}{identity.loggedIn}"/>
                </ice:panelGroup>
            </ice:panelGrid>
			
</div>

