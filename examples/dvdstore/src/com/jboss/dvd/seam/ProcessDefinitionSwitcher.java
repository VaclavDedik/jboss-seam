package com.jboss.dvd.seam;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Jbpm;
import org.jbpm.JbpmContext;

/**
 * Switches JBPM process definitions dynamically
 */
@Name("processDefinitionSwitcher")
@Scope(ScopeType.APPLICATION)
public class ProcessDefinitionSwitcher
{
    static final String[] ORDER_DEFS = { 
          "ordermanagement1.jpdl.xml", 
          "ordermanagement2.jpdl.xml", 
          "ordermanagement3.jpdl.xml" 
       };

    @In(create=true, value="org.jboss.seam.core.jbpm")
    private Jbpm jbpm;
    
    @In(create=true)
    private JbpmContext jbpmContext;
    
    public List<SelectItem> getProcessDefinitions()
    {
       List<SelectItem> result = new ArrayList<SelectItem>();
       for (String def: ORDER_DEFS)
       {
          result.add( new SelectItem(def, def.substring(0, def.length()-9)) );
       }
       return result;
    }
    
    private String currentProcessDefinition;
    
    public String getCurrentProcessDefinition()
    {
       return currentProcessDefinition;
    }
    
    public void setCurrentProcessDefinition(String def)
    {
       currentProcessDefinition = def;
    }

    public String switchProcess() 
    {
       jbpmContext.deployProcessDefinition( jbpm.getProcessDefinitionFromResource(currentProcessDefinition) );
       return null;
    }
    
}

