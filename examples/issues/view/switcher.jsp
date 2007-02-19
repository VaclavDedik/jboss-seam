
     <table class="rvgMenu">
     <tr>
     
      <td class="rvgLinks">
      
       <h:outputLink value="home.jsf"><h:outputText value="#{messages.Home}"/></h:outputLink>
       <h:outputText value=" | "/> 
       <h:outputLink value="findIssue.jsf"><h:outputText value="#{messages.Find} #{messages.Issues}"/></h:outputLink>
       <h:outputText value=" | "/> 
       <h:outputLink value="editIssue.jsf" rendered="#{identity.loggedIn}"><h:outputText value="#{messages.Create} #{messages.Issue}"/></h:outputLink> 
       <h:outputText value=" | " rendered="#{identity.loggedIn}"/>
       <h:outputLink value="login.jsf" rendered="#{not identity.loggedIn}"><h:outputText value="#{messages.Login}"/></h:outputLink>
       <h:commandLink value="#{messages.Logout}" action="#{identity.logout}" rendered="#{identity.loggedIn}"/>

       <t:dataList value="#{conversationStack}" var="entry">
         <h:outputText value=" | "/> 
         <h:commandLink value="#{entry.description}" action="#{entry.select}"/>
       </t:dataList>

      </td>
      
      <td class="rvgSwitch">
      
       <h:selectOneMenu value="#{localeSelector.localeString}">
         <f:selectItems value="#{localeSelector.supportedLocales}"/>
       </h:selectOneMenu>
       <h:commandButton action="#{localeSelector.select}" value="#{messages.Switch}"/>
       
       <h:selectOneMenu value="#{switcher.conversationIdOrOutcome}">
         <f:selectItem itemLabel="#{messages.Find} #{messages.Issues}" itemValue="findIssue"/>
         <f:selectItem itemLabel="#{messages.Create} #{messages.Issue}" itemValue="editIssue" itemDisabled="#{not identity.loggedIn}"/>
         <f:selectItem itemLabel="#{messages.Browse} #{messages.Projects}" itemValue="findProject"/>
         <f:selectItem itemLabel="#{messages.Create} #{messages.Project}" itemValue="editProject" itemDisabled="#{not identity.loggedIn}"/>
         <f:selectItems value="#{switcher.selectItems}"/>
       </h:selectOneMenu>
       <h:commandButton action="#{switcher.select}" value="#{messages.Switch}"/>
       
      </td>
     
     </tr>
     </table>
