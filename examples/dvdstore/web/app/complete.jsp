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
    <!-- <h1><h:outputText value="#{msgs.storeHeader}" /></h1> -->

    <h2><h:outputText value="#{msgs.checkoutCompleteHeader}" /></h2>
    <h:form>
        <h:dataTable value="#{cart.cart}" 
                     var="item"
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
                <h:outputText value="#{item.item.quantity}" />
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
        </h:dataTable>
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

    <p>
        <h:outputText value="#{cart.order.totalAmount}"> 
            <f:convertNumber type="currency" currencySymbol="$" />
        </h:outputText>
        <h:outputText value="#{msgs.chargedToCard}" />
        <h:outputText value="#{cart.order.customer.creditCard}" />
        (<h:outputText value="#{cart.order.customer.creditCardTypeString}" />), 
        <h:outputText value="#{msgs.expiration}" />
        <h:outputText value="#{cart.order.customer.creditCardExpiration}" />
    </p>

    <p>
        <h:outputText value="#{msgs.orderNumber}" />
        <h:outputText value="#{cart.order.orderId}" />
    </p>

    <h:form>
        <h:commandButton action="#{cart.resetCart}" value="#{msgs.shopAgainButton}" />
        <h:commandButton action="#{login.logout}"  value="#{msgs.logoutButton}" />
    </h:form>

    <%@ include file="/foot.jsp" %> 
</body>
</html>
</f:view>
