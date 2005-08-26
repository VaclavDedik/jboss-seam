<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
                      "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<f:view>
<html>
<head>
    <f:loadBundle basename="com.jboss.dvd.web.store" var="msgs" />     
    <link href="<c:url value="/style.css" />" rel="stylesheet" type="text/css" />
    <title><h:outputText value="#{msgs.newCustomerTitle}" /></title>
</head>
<body>
    <%@ include file="head.jsp" %> 

    <h:messages globalOnly="true" 
                layout="list" 
                errorClass="error"/>

    <h2><h:outputText value="#{msgs.newCustomerHeader}" /></h2>
    <h3><h:outputText value="#{msgs.newCustomerInfo}" /></h3>

    <h:form>
        <h:panelGrid columns="2">
            <h:outputText value="#{msgs.newCustomerFN}" />
            <h:panelGroup>
                <h:inputText id="firstName" required="true"
                             value="#{editCustomer.customer.firstName}" />
                <f:verbatim>*</f:verbatim>
                <h:message for="firstName" styleClass="error" />
            </h:panelGroup>

            <h:outputText value="#{msgs.newCustomerLN}" />
            <h:panelGroup>
                <h:inputText id="lastName" required="true"
                             value="#{editCustomer.customer.lastName}" />
                <f:verbatim>*</f:verbatim>
                <h:message for="lastName" styleClass="error" />
            </h:panelGroup>

            <h:outputText value="#{msgs.newCustomerA1}" />
            <h:panelGroup>
                <h:inputText id="address1" required="true"
                             value="#{editCustomer.customer.address1}" />
                <f:verbatim>*</f:verbatim>
                <h:message for="address1" styleClass="error" />
            </h:panelGroup>

            <h:outputText value="#{msgs.newCustomerA2}" />
            <h:panelGroup>
                <h:inputText value="#{editCustomer.customer.address2}" />
            </h:panelGroup>

            <h:outputText value="#{msgs.newCustomerCY}" />
            <h:panelGroup>
                <h:inputText id="city" required="true" 
                             value="#{editCustomer.customer.city}" />
                <f:verbatim>*</f:verbatim>
                <h:message for="city" styleClass="error" />
            </h:panelGroup>

            <h:outputText value="#{msgs.newCustomerST}" />
            <h:inputText value="#{editCustomer.customer.state}" />

            <h:outputText value="#{msgs.newCustomerZP}" />
            <h:inputText value="#{editCustomer.customer.zip}" />

            <h:outputText value="#{msgs.newCustomerCO}" />
            <h:selectOneMenu value="#{editCustomer.customer.country}">
                <f:selectItem itemValue="United States" itemLabel="#{msgs.co_US}"/>
                <f:selectItem itemValue="Australia" itemLabel="#{msgs.co_AU}" />
                <f:selectItem itemValue="Canada" itemLabel="#{msgs.co_CA}" />
                <f:selectItem itemValue="Chile" itemLabel="#{msgs.co_CL}" />
                <f:selectItem itemValue="China" itemLabel="#{msgs.co_CH}" />
                <f:selectItem itemValue="France" itemLabel="#{msgs.co_FR}" />
                <f:selectItem itemValue="Germany" itemLabel="#{msgs.co_DE}" />
                <f:selectItem itemValue="Japan" itemLabel="#{msgs.co_JP}" />
                <f:selectItem itemValue="Russia" itemLabel="#{msgs.co_RU}" />
                <f:selectItem itemValue="South Africa" itemLabel="#{msgs.co_ZA}" />
                <f:selectItem itemValue="UK" itemLabel="#{msgs.co_UK}" />
            </h:selectOneMenu> 

            <h:outputText value="#{msgs.newCustomerEM}" />
            <h:inputText value="#{editCustomer.customer.email}" />

            <h:outputText value="#{msgs.newCustomerPH}" />
            <h:inputText value="#{editCustomer.customer.phone}" />

            <h:outputText value="#{msgs.newCustomerUN}" />
            <h:panelGroup>
                <h:inputText id="userName" required="true" 
                             value="#{editCustomer.customer.userName}">
                    <f:validateLength minimum="4" maximum="16"/> 
                </h:inputText>
                <f:verbatim>*</f:verbatim>
                <h:message for="userName" styleClass="error" />
            </h:panelGroup>

            <h:outputText value="#{msgs.newCustomerPW}" />
            <h:panelGroup>
                <h:inputText id="password" required="true"
                             value="#{editCustomer.password}">
                    <f:validateLength minimum="8" /> 
                </h:inputText>
                <f:verbatim>*</f:verbatim>
                <h:message for="password" styleClass="error" />
            </h:panelGroup>
            
            <h:outputText value="#{msgs.newCustomerCCT}" />
            <h:selectOneMenu value="#{editCustomer.creditCardType}">
                <f:selectItems value="#{editCustomer.creditCardTypes}" />
            </h:selectOneMenu> 

           
            <h:outputText value="#{msgs.newCustomerCCN}" />
            <h:panelGroup>
                <h:inputText id="creditCard" required="true" 
                             value="#{editCustomer.customer.creditCard}" />
                <f:verbatim>*</f:verbatim>
                <h:message for="creditCard" styleClass="error" />
            </h:panelGroup>


            <h:outputText value="#{msgs.newCustomerCCE}" />
            <h:panelGroup>
                <h:selectOneMenu value="#{editCustomer.creditCardMonth}">
                    <f:selectItem itemValue="1" itemLabel="#{msgs.mo_1}" />
                    <f:selectItem itemValue="2" itemLabel="#{msgs.mo_2}" />
                    <f:selectItem itemValue="3" itemLabel="#{msgs.mo_3}" />
                    <f:selectItem itemValue="4" itemLabel="#{msgs.mo_4}" />
                    <f:selectItem itemValue="5" itemLabel="#{msgs.mo_5}" />
                    <f:selectItem itemValue="6" itemLabel="#{msgs.mo_6}" />
                    <f:selectItem itemValue="7" itemLabel="#{msgs.mo_7}" />
                    <f:selectItem itemValue="8" itemLabel="#{msgs.mo_8}" />
                    <f:selectItem itemValue="9" itemLabel="#{msgs.mo_9}" />
                    <f:selectItem itemValue="10" itemLabel="#{msgs.mo_10}" />
                    <f:selectItem itemValue="11" itemLabel="#{msgs.mo_11}" />
                    <f:selectItem itemValue="12" itemLabel="#{msgs.mo_12}" />
                </h:selectOneMenu> 

                <h:selectOneMenu value="#{editCustomer.creditCardYear}">
                    <f:selectItem itemValue="2005" />
                    <f:selectItem itemValue="2006" />
                    <f:selectItem itemValue="2007" />
                    <f:selectItem itemValue="2008" />
                    <f:selectItem itemValue="2009" />
                    <f:selectItem itemValue="2010" />
                </h:selectOneMenu>
            </h:panelGroup>
            
            <h:outputText value="#{msgs.newCustomerAG}" />
            <h:inputText value="#{editCustomer.customer.age}" />

            <h:outputText value="#{msgs.newCustomerIN}" />
            <h:inputText value="#{editCustomer.customer.income}" />


            <h:outputText value="#{msgs.newCustomerGE}" />
            <h:selectOneMenu value="#{editCustomer.customer.gender}">
                <f:selectItem itemValue="M" itemLabel="#{msgs.gender_m}" />
                <f:selectItem itemValue="F" itemLabel="#{msgs.gender_f}" />
            </h:selectOneMenu> 
        </h:panelGrid>
        
        <h:commandButton action="#{editCustomer.create}" value="#{msgs.newCustomerSubmit}" />
    </h:form>
 
    <%@ include file="foot.jsp" %> 
</body>
</html>
</f:view>

