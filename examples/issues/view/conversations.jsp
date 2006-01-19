     <div class="rvgList">
      <h:form>
	    <span class="rvgResultsNone">
	      <h:outputText value="#{msg.No} #{msg.Workspace}" rendered="#{empty conversationList}"/>
	    </span>
	 
        <h:dataTable value="#{conversationList}" var="entry" rowClasses="rvgRowOne,rvgRowTwo" 
              rendered="#{not empty conversationList}">
            <h:column>
			    <f:facet name="header">
			        <h:outputText value="#{msg.Workspace}"/>
			    </f:facet>
                <h:commandLink action="#{entry.select}" value="#{entry.description}"/>
                <!-- h:outputText value="[current]" rendered="#{entry.current}"/-->
            </h:column>
            <h:column>
			    <f:facet name="header">
			        <h:outputText value="#{msg.Workspace_activity}"/>
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
			        <h:outputText value="#{msg.Action}"/>
			    </f:facet>
			    <h:commandButton action="#{entry.select}" value="#{msg.Switch}"/>
			    <h:commandButton action="#{entry.destroy}" value="#{msg.Destroy}"/>
		    </h:column>
        </h:dataTable>
        <!-- a href="main.jsf">New workspace</a-->
      </h:form>
     </div>
