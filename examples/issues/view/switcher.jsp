
     <table class="rvgMenu">
     <tr>
     
      <td class="rvgLinks">
      
       <h:outputLink value="home.jsf"><h:outputText value="#{messages.Home}"/></h:outputLink>
       <h:outputText value=" | "/> 
       <h:outputLink value="findIssue.jsf"><h:outputText value="#{messages.Find} #{messages.Issues}"/></h:outputLink>
       <h:outputText value=" | "/> 
       <h:outputLink value="editIssue.jsf" rendered="#{loggedIn!=null}"><h:outputText value="#{messages.Create} #{messages.Issue}"/></h:outputLink> 
       <h:outputText value=" | " rendered="#{loggedIn!=null}"/>
       <h:outputLink value="login.jsf" rendered="#{loggedIn==null}"><h:outputText value="#{messages.Login}"/></h:outputLink>
       <h:commandLink value="#{messages.Logout}" action="#{login.logout}" rendered="#{loggedIn!=null}"/>

       <t:dataList value="#{conversationStack}" var="entry">
         <h:outputText value=" | "/> 
         <h:commandLink value="#{entry.description}" action="#{entry.select}"/>
       </t:dataList>

      </td>
      
      <td class="rvgSwitch">
       <h:selectOneMenu value="#{switcher.conversationIdOrOutcome}">
         <f:selectItem itemLabel="#{messages.Find} #{messages.Issues}" itemValue="findIssue"/>
         <f:selectItem itemLabel="#{messages.Create} #{messages.Issue}" itemValue="editIssue" itemDisabled="#{loggedIn==null}"/>
         <f:selectItem itemLabel="#{messages.Browse} #{messages.Projects}" itemValue="findProject"/>
         <f:selectItem itemLabel="#{messages.Create} #{messages.Project}" itemValue="editProject" itemDisabled="#{loggedIn==null}"/>
         <f:selectItems value="#{switcher.selectItems}"/>
       </h:selectOneMenu>
       <h:commandButton action="#{switcher.select}" value="#{messages.Switch}"/>
       
      </td>
     
     </tr>
     </table>
