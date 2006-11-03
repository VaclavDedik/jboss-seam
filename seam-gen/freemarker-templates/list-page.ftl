<#assign pound = "#">
<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
                             "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
                xmlns:s="http://jboss.com/products/seam/taglib"
                xmlns:ui="http://java.sun.com/jsf/facelets"
                xmlns:f="http://java.sun.com/jsf/core"
                xmlns:h="http://java.sun.com/jsf/html"
                template="layout/template.xhtml">
                       
<ui:define name="body">

    <h1>${masterPageName}</h1>
    <p>Generated list page.</p>
    
    <h:dataTable value="${pound}{${componentName}List.resultList}" var="item">
        <h:column>
            <f:facet name="header">Id</f:facet>
            ${pound}{item.id}
        </h:column>
        <h:column>
            <f:facet name="header">Name</f:facet>
            <s:link value="${pound}{item.name}" view="/${pageName}.xhtml">
                <f:param name="${componentName}Id" value="${pound}{item.id}"/>
            </s:link>
        </h:column>
    </h:dataTable>
    <div class="actionButtons">
        <s:link id="done" value="Create ${actionName}" linkStyle="button"
            view="/${pageName}.xhtml"/>			  
    </div>
    
</ui:define>

</ui:composition>

