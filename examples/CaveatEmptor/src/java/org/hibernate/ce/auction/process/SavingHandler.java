/*
 * JBoss, the OpenSource webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.hibernate.ce.auction.process;

import javax.faces.context.FacesContext;

import org.hibernate.ce.auction.model.Item;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
public class SavingHandler implements ActionHandler
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -4152746327411917114L;

   public void execute(ExecutionContext executionContext) throws Exception
   {
      // Get the item and save it
      Item item = (Item)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("item");
      JsfItemManager jsfItemManager = (JsfItemManager)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("jsfItemManager");
      if (item != null)
      {
         System.out.println("item != null item.name: " + item.getName());
         jsfItemManager.setItem(item);
         Process.save(jsfItemManager.getProcessInstanceId(), item);
      }
      else
      {
         item = jsfItemManager.getItem();
         System.out.println("item == null item.name: " + item.getName());
         Process.save(jsfItemManager.getProcessInstanceId(), item);
      }
   }

}



