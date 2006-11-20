<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://jboss.com/products/seam/taglib" prefix="s" %>
<f:view>

<div class="section">
  <h:form>
    <span class="errors"><h:messages globalOnly="true"/></span>
    <h1>Search Hotels</h1>
    <fieldset> 
        <h:inputText value="#{hotelBooking.searchString}" style="width: 165px;" />&nbsp;
        <h:commandButton value="Find Hotels" action="#{hotelBooking.find}" styleClass="button" />&nbsp;
        <h:commandButton value="Clear Results" action="#{hotelBooking.clear}" styleClass="button" />
    </fieldset>
  </h:form>
</div>

<div class="section">
  <h:form>
    <h:outputText value="No Hotels Found" rendered="#{hotels != null and hotels.rowCount==0}"/>
    <h:dataTable value="#{hotels}" var="hot" rendered="#{hotels.rowCount>0}">
        <h:column>
            <f:facet name="header">Name</f:facet>
            <h:outputText value="#{hot.name}"/>
        </h:column>
        <h:column>
            <f:facet name="header">Address</f:facet>
            <h:outputText value="#{hot.address}"/>
        </h:column>
        <h:column>
            <f:facet name="header">City, State</f:facet>
            <h:outputText value="#{hot.city}, #{hot.state}"/>
        </h:column> 
        <h:column>
            <f:facet name="header">Zip</f:facet>
            <h:outputText value="#{hot.zip}"/>
        </h:column>
        <h:column>
            <f:facet name="header">Action</f:facet>
            <h:commandLink action="#{hotelBooking.selectHotel}" value="View Hotel"/>
        </h:column>
    </h:dataTable>
  </h:form>
</div>

<div class="section">
    <h1>Current Hotel Bookings</h1>
</div>
<div class="section">
  <h:form>
    <h:outputText value="No Bookings Found" rendered="#{bookings.rowCount==0}"/>
    <h:dataTable value="#{bookings}" var="book" rendered="#{bookings.rowCount>0}">
        <h:column>
            <f:facet name="header">Name</f:facet>
            <h:outputText value="#{book.hotel.name}"/>
        </h:column>
        <h:column>
            <f:facet name="header">Address</f:facet>
            <h:outputText value="#{book.hotel.address}"/>
        </h:column>
        <h:column>
            <f:facet name="header">City, State</f:facet>
            <h:outputText value="#{book.hotel.city}, #{book.hotel.state}"/>
        </h:column>
        <h:column>
            <f:facet name="header">Check in date</f:facet>
            <h:outputText value="#{book.checkinDate}">
                <s:convertDateTime type="date"/>
            </h:outputText>
        </h:column>
        <h:column>
            <f:facet name="header">Check out date</f:facet>
            <h:outputText value="#{book.checkoutDate}">
                <s:convertDateTime type="date"/>
            </h:outputText>
        </h:column>
        <h:column>
            <f:facet name="header">Confirmation number</f:facet>
            <h:outputText value="#{book.id}"/>
        </h:column>
        <h:column>
            <f:facet name="header">Action</f:facet>
            <h:commandLink action="#{bookingList.cancel}" value="Cancel"/>
        </h:column>
    </h:dataTable>
  </h:form>
</div>


</f:view>