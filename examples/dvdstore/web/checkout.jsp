<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
                      "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<f:view>     
<html>
<head>
    <link href="<c:url value="/style.css" />" rel="stylesheet" type="text/css" />
    <f:loadBundle basename="com.jboss.dvd.web.store" var="msgs" />     
    <title><h:outputText value="#{msgs.checkoutTitle}" /></title>
</head>
<body> 
    <%@ include file="/head.jsp" %> 

    <h:messages globalOnly="true" 
                layout="list" 
                errorClass="error"/>

    <h2><h:outputText value="#{msgs.checkoutHeader}" /></h2>
    <h:form>
        <h:dataTable value="#{cart.cart}" var="item"
                     styleClass="dvdtable" 
                     headerClass="dvdtablehead"
                     rowClasses="dvdtableodd,dvdtableeven"
                     columnClasses="dvdtablecol">
            <h:column>
                <f:facet name="header">
                    <h:outputText value="#{msgs.cartItemColumn}" />
                </f:facet>
                <h:outputText value="#{item.item.position}" />
            </h:column>                        
            <h:column>
                <f:facet name="header">
                    <h:outputText value="#{msgs.cartQuantityColumn}" />
                </f:facet>
                <h:inputText value="#{item.item.quantity}" size="6" />
            </h:column>                        
            <h:column>
                <f:facet name="header">
                    <h:outputText value="#{msgs.cartTitleColumn}" />
                </f:facet>
                <h:outputText value="#{item.item.product.title}" />
            </h:column>                        
            <h:column>
                <f:facet name="header">
                    <h:outputText value="#{msgs.cartActorColumn}" />
                </f:facet>
                <h:outputText value="#{item.item.product.actor}" />
            </h:column>                        

            <h:column>
                <f:facet name="header">
                    <h:outputText value="#{msgs.cartPriceColumn}" />
                </f:facet>
                <h:outputText value="#{item.item.product.price}">
                    <f:convertNumber type="currency" currencySymbol="$" />
                </h:outputText>
            </h:column>                        
            <h:column>
                <f:facet name="header">
                    <h:outputText value="#{msgs.cartRemoveColumn}" />
                </f:facet>
                <h:selectBooleanCheckbox value="#{item.selected}"/>
            </h:column>
        </h:dataTable>
        <h:commandButton action="#{cart.updateCart}" value="#{msgs.checkoutUpdateButton}" />
    </h:form>

    <h:panelGrid columns="2">
        <h:outputText value="#{msgs.checkoutSubtotal}" />
        <h:outputText value="#{cart.subtotal}">
            <f:convertNumber type="currency" currencySymbol="$" />
        </h:outputText>

        <h:outputText value="#{msgs.checkoutTax}" />
        <h:outputText value="#{cart.tax}">
            <f:convertNumber type="currency" currencySymbol="$" />
        </h:outputText>

        <h:outputText value="#{msgs.checkoutTotal}" />
        <h:outputText value="#{cart.total}">
            <f:convertNumber type="currency" currencySymbol="$" />
        </h:outputText>
    </h:panelGrid>

    <h:form>
        <h:commandButton action="browse"             value="#{msgs.shopAgainButton}" />
        <h:commandButton action="#{cart.purchase}"   value="#{msgs.checkoutPurchaseButton}" />
    </h:form>

    <%@ include file="/foot.jsp" %> 
</body>
</html>
</f:view>
