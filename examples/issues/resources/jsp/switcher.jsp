
     <table class="rvgMenu">
     <tr>
     
      <td class="rvgLinks">
      
       <a href="home.jsf">Home</a> | <a href="findIssue.jsf">Find Issues</a> | <a href="editIssue.jsf">Create Issue</a>

       | 
       <h:outputLink value="login.jsf" rendered="#{loggedIn==null}"><h:outputText value="Login"/></h:outputLink>
       <h:commandLink value="Logout" action="#{login.logout}" rendered="#{loggedIn!=null}"/>

       <t:dataList value="#{conversationStack}" var="entry">
         <h:outputText value=" | "/> 
         <h:commandLink value="#{entry.description}" action="#{entry.select}"/>
       </t:dataList>

      </td>
      
      <td class="rvgSwitch">
       <h:selectOneMenu value="#{switcher.conversationIdOrOutcome}">
         <f:selectItem itemLabel="Find Issues" itemValue="findIssue"/>
         <f:selectItem itemLabel="Create Issue" itemValue="editIssue"/>
         <f:selectItem itemLabel="Browse Projects" itemValue="findProject"/>
         <f:selectItem itemLabel="Create Project" itemValue="editProject"/>
         <f:selectItems value="#{switcher.selectItems}"/>
       </h:selectOneMenu>
       <h:commandButton action="#{switcher.select}" value="Switch"/>
       
      </td>
     
     </tr>
     </table>
