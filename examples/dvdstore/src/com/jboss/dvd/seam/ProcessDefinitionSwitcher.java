package com.jboss.dvd.seam;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Jbpm;

/**
 * Switches JBPM process definitions dynamically
 */
@Name("processDefinitionSwitcher")
@Scope(ScopeType.APPLICATION)
public class ProcessDefinitionSwitcher
{
    static final String[] ORDER_DEFS = { 
          "jbpm-ordermanagement1.xml", 
          "jbpm-ordermanagement2.xml", 
          "jbpm-ordermanagement3.xml" 
       };

    @In(create=true, value="org.jboss.seam.core.jbpm")
    private Jbpm jbpm;
    
    public List<SelectItem> getProcessDefinitions()
    {
       List<SelectItem> result = new ArrayList<SelectItem>();
       for (String def: ORDER_DEFS)
       {
          result.add( new SelectItem(def, def.substring(5, def.length()-4)) );
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

    public String switchProcess() {
       System.out.println("Switching to process definition: " + currentProcessDefinition); 
       jbpm.loadProcessDefinition(currentProcessDefinition, true);
       return "admin";
    }
    
}

