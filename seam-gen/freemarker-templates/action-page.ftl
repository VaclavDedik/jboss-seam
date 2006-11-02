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
    <h1>Seam Generated Page named: "${pageName}"</h1>
    <h:form>
        <div>
            <h:commandButton styleClass="formButton" type="submit"
                  value="Test Action" action="${pound}{${actionName}.go}"/>     			  
        </div>
    </h:form>
</ui:define>

</ui:composition>

