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
    <title><h:outputText value="#{msgs.browseTitle}" /></title>
</head>
<body>
    <%@ include file="/head.jsp" %> 
    <!-- <h1><h:outputText value="#{msgs.storeHeader}" /></h1> -->
 
    <h1><h:outputText value="#{testValue}" /></h1> 

    <h2><h:outputText value="#{msgs.browseSearchHeader}" /></h2>
    <h:form>
        <h:panelGrid columns="2">
            <h:outputText value="#{msgs.searchTitle}" />
            <h:inputText value="#{search.browseTitle}" size="15"/>
            
            <h:outputText value="#{msgs.searchActor}" />
            <h:inputText value="#{search.browseActor}" size="15"/>
                        
            <h:outputText value="#{msgs.searchCategory}" />
            <h:selectOneMenu value="#{search.browseCategory}">
                <f:selectItems value="#{search.categories}" />
            </h:selectOneMenu>
        </h:panelGrid>
        
        <h:commandButton action="#{search.doSearch}" value="#{msgs.searchButton}" /> 
    </h:form>

    <f:subview id="search" rendered="#{searchResults!=null}">
        <h:form>
                <h2>
                    <h:outputText value="#{msgs.searchResultsHeader}"   
                                  rendered="#{!empty(searchResults)}"/>
                    <h:outputText value="#{msgs.noSearchResultsHeader}"
                                  rendered="#{empty(searchResults)}"/>
                </h2>

            <div>
                <h:commandButton value="#{msgs.prevPageButton}" 
                                 action="#{search.prevPage}" disabled="#{search.firstPage}" />
                <h:commandButton value="#{msgs.nextPageButton}" 
                                 action="#{search.nextPage}" disabled="#{search.lastPage}"  />
            </div>
            <h:dataTable rendered="#{!empty(searchResults)}"
                         value="#{searchResults}" 
                         var="product" 
                         styleClass="dvdtable" 
                         headerClass="dvdtablehead"
                         rowClasses="dvdtableodd,dvdtableeven"
                         columnClasses="dvdtablecol">
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="#{msgs.searchResultsAdd}" />
                    </f:facet>
                    <h:selectBooleanCheckbox value="#{product.selected}"/>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="#{msgs.searchResultsTitle}" />
                    </f:facet>
                    <h:outputText value="#{product.item.title}" />
                </h:column>                        
                
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="#{msgs.searchResultsActor}" />
                    </f:facet>
                    <h:outputText value="#{product.item.actor}" />
                </h:column>                        

                <h:column>
                    <f:facet name="header">
                        <h:outputText value="#{msgs.searchResultsPrice}" />
                    </f:facet>
                    <h:outputText value="#{product.item.price}">
                        <f:convertNumber type="currency" currencySymbol="$" />
                    </h:outputText>
                </h:column>                        
            </h:dataTable>

            <h:commandButton rendered="#{!empty(searchResults)}"
                             action="#{search.addToCart}" value="#{msgs.searchUpdateButton}"/>
            
        </h:form>        
    </f:subview>

    <f:subview id="cart" rendered="#{!cart.isEmpty}">
        <div class ="cart">
        <h:form>
            <f:verbatim><h2><h:outputText value="#{msgs.searchCartHeader}" /></h2></f:verbatim>
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
                        <h:outputText value="#{msgs.cartTitleColumn}" />
                    </f:facet>
                    <h:outputText value="#{item.item.product.title}" />
                </h:column>                        
            </h:dataTable>
            <h:commandButton action="#{search.checkout}" value="#{msgs.checkoutButton}" />
        </h:form>
        </div>
    </f:subview>

    <%@ include file="/foot.jsp" %> 
</body>
</html>
</f:view>
