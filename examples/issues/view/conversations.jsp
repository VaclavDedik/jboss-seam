     <div class="rvgList">
      <h:form>
	    <span class="rvgResultsNone">
	      <h:outputText value="#{messages.No} #{messages.Workspace}" rendered="#{empty conversationList}"/>
	    </span>
	 
        <h:dataTable value="#{conversationList}" var="entry" rowClasses="rvgRowOne,rvgRowTwo" 
              rendered="#{not empty conversationList}">
            <h:column>
			    <f:facet name="header">
			        <h:outputText value="#{messages.Workspace}"/>
			    </f:facet>
                <h:commandLink action="#{entry.select}" value="#{entry.description}"/>
                <%-- h:outputText value="[current]" rendered="#{entry.current}"/--%>
            </h:column>
            <h:column>
			    <f:facet name="header">
			        <h:outputText value="#{messages.Workspace_activity}"/>
			    </f:facet>
                <h:outputText value="#{entry.startDatetime}">
                    <f:convertDateTime type="time" pattern="hh:mm a"/>
                </h:outputText>
                <h:outputText value=" - "/>
                <h:outputText value="#{entry.lastDatetime}">
                    <f:convertDateTime type="time" pattern="hh:mm a"/>
                </h:outputText>
            </h:column>
		    <h:column>
			    <f:facet name="header">
			        <h:outputText value="#{messages.Action}"/>
			    </f:facet>
			    <h:commandButton action="#{entry.select}" value="#{messages.Switch}"/>
			    <h:commandButton action="#{entry.destroy}" value="#{messages.Destroy}"/>
		    </h:column>
        </h:dataTable>
        <!-- a href="main.jsf">New workspace</a-->
      </h:form>
     </div>
