	 <div class="rvgList">
     
	 <span class="rvgResultsNone">
	   <h:outputText value="#{messages.No} #{messages.Project}" rendered="#{projectList.rowCount==0}"/>
	 </span>
	 
	 <h:dataTable value="#{projectList}" var="project" rendered="#{projectList.rowCount>0}" 
	       rowClasses="rvgRowOne,rvgRowTwo" headerClass="rvgOrder">
		<h:column>
			<f:facet name="header">
			    <h:outputText value="#{messages.Project_name}"/>
			</f:facet>
			<h:outputText value="#{project.name}"/>
		</h:column>
		<h:column>
			<f:facet name="header">
			    <h:outputText value="#{messages.Project_description}"/>
			</f:facet>
			<h:commandLink value="#{project.description}" action="#{projectSelector.select}"/>
		</h:column>
		<h:column>
			<f:facet name="header">
			    <h:outputText value="#{messages.Project_issues}"/>
			</f:facet>
			<h:outputText value="#{project.issueCount}"/>
		</h:column>
		<h:column>
			<f:facet name="header"><h:outputText value="#{messages.Action}"/></f:facet>
			<h:commandButton action="#{projectSelector.select}" value="#{projectSelector.buttonLabel}"/>
		</h:column>
	 </h:dataTable>

	 <span class="rvgPage">
	   <h:commandButton action="#{projectEditor.createProject}" value="#{messages.Create}" rendered="#{projectSelector.createEnabled}"/>
	   <h:commandButton action="#{projectSelector.cancel}" value="#{messages.Cancel}" rendered="#{!projectSelector.createEnabled}"/>
	 </span>
	 
	 </div>
