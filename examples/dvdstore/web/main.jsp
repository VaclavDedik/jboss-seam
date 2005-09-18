<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
                      "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="full_header" value="true" />
<f:view>     
<html>
<head>
    <link href="<c:url value="/style.css" />" rel="stylesheet" type="text/css" />
    <f:loadBundle basename="com.jboss.dvd.web.store" var="msgs" />     
    <title><h:outputText value="#{msgs.mainTitle}" /></title>
</head>
<body>
    <%@ include file="/head.jsp" %> 
    <!-- <h1><h:outputText value="#{msgs.storeHeader}" /></h1> -->
    <h2><h:outputText value="#{msgs.mainHeader}" /></h2>

    <f:subview id="previous" rendered="#{!empty(phistory.recentProducts)}">
        <h3><h:outputText value="#{msgs.previousPurchasesHeader}" /></h3>
        <h:dataTable value="#{phistory.recentProducts}" var="product"
                     styleClass="dvdtable" 
                     headerClass="dvdtablehead"
                     rowClasses="dvdtableodd,dvdtableeven"
                     columnClasses="dvdtablecol">
                                                                                                             
            <h:column>
                <f:facet name="header">
                    <h:outputText value="#{msgs.historyTitleColumn}" />
                </f:facet>
                <h:outputText value="#{product.title}" />
            </h:column>                        
            
            <h:column>
                <f:facet name="header">
                    <h:outputText value="#{msgs.historyActorColumn}" />
                </f:facet>
                <h:outputText value="#{product.actor}" />
            </h:column>                        

            <h:column>
                <f:facet name="header">
                    <h:outputText value="#{msgs.historyRelatedColumn}" />
                </f:facet>
                <h:outputText value="#{product.relatedProduct.title}" />
            </h:column>                        
        </h:dataTable>            
    </f:subview>
    <br />

    <h:form>
        <h:commandButton action="#{search.start}" value="#{msgs.startShoppingButton}" />   
    </h:form>

    <%@ include file="/foot.jsp" %> 
</body>
</html>
</f:view>
