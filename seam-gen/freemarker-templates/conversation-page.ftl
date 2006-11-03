<#assign pound = "#">
<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
                             "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
                xmlns:s="http://jboss.com/products/seam/taglib"
                xmlns:ui="http://java.sun.com/jsf/facelets"
                xmlns:f="http://java.sun.com/jsf"
                xmlns:h="http://java.sun.com/jsf/html"
                template="layout/template.xhtml">
                       
<ui:define name="body">

    <h1>${pageName}</h1>
    <p>Generated conversation page.</p>
    <div class="dialog">
        <div class="prop">
            <span class="name">Value</span>
            <span class="value">${pound}{${componentName}.value}</span>
        </div>
    </div>
    <h:form id="${componentName}">
        <div class="actionButtons">
            <h:commandButton id="begin" value="Begin" 
                action="${pound}{${componentName}.begin}"/>     			  
            <h:commandButton id="inc" value="Increment" 
                action="${pound}{${componentName}.increment}"/>     			  
            <h:commandButton id="end" value="End" 
                action="${pound}{${componentName}.end}"/>     			  
        </div>
    </h:form>
    
</ui:define>

</ui:composition>

