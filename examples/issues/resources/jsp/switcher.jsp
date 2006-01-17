
     <table class="rvgMenu">
     <tr>
     
      <td class="rvgLinks">
      
       <h:outputLink value="home.jsf"><h:outputText value="Home"/></h:outputLink>
       <h:outputText value=" | "/> 
       <h:outputLink value="findIssue.jsf"><h:outputText value="Find Issues"/></h:outputLink>
       <h:outputText value=" | "/> 
       <h:outputLink value="editIssue.jsf" rendered="#{loggedIn!=null}"><h:outputText value="Create Issue"/></h:outputLink> 
       <h:outputText value=" | " rendered="#{loggedIn!=null}"/>
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
         <f:selectItem itemLabel="Create Issue" itemValue="editIssue" itemDisabled="#{loggedIn==null}"/>
         <f:selectItem itemLabel="Browse Projects" itemValue="findProject"/>
         <f:selectItem itemLabel="Create Project" itemValue="editProject" itemDisabled="#{loggedIn==null}"/>
         <f:selectItems value="#{switcher.selectItems}"/>
       </h:selectOneMenu>
       <h:commandButton action="#{switcher.select}" value="Switch"/>
       
      </td>
     
     </tr>
     </table>
