<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<html>
<head>
<title>Todo List</title>
</head>
<body>
<h1>Todo List</h1>
<f:view>
	<h:form id="list">
	  <div>
	    <h:outputText value="There are no todo items." rendered="#{empty taskInstanceList}"/>
        <h:dataTable value="#{taskInstanceList}" var="task" rendered="#{not empty taskInstanceList}">
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Description"/>
                </f:facet>
                <h:outputText value="#{task.taskMgmtInstance.processInstance.contextInstance.variables['description']}"/>
            </h:column>
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Created"/>
                </f:facet>
                <h:outputText value="#{task.taskMgmtInstance.processInstance.start}">
                    <f:convertDateTime type="date"/>
                </h:outputText>
            </h:column>
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Priority"/>
                </f:facet>
                <h:inputText value="#{task.priority}" style="width: 30"/>
            </h:column>
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Due Date"/>
                </f:facet>
                <h:inputText value="#{task.dueDate}" style="width: 100">
                    <f:convertDateTime type="date" dateStyle="short"/>
                </h:inputText>
            </h:column>
            <h:column>
                <h:commandLink action="#{todoList.done}"> 
                    <h:commandButton value="Done"/>
                    <f:param name="taskId" value="#{task.id}"/>
                </h:commandLink>
            </h:column>
        </h:dataTable>
      </div>
      <div>
      <h:messages/>
      </div>
      <div>
         <h:commandButton value="Update Items" action="update"/>
      </div>
   </h:form>
   <h:form id="new">
	  <div>
	    <h:inputText value="#{todoList.description}"/>
	    <h:commandButton value="Create New Item" action="#{todoList.createTodo}"/>
	  </div>
	</h:form>
</f:view>
</body>
</html>
