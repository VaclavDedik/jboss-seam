/*
 * JBoss, the OpenSource webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.hibernate.ce.auction.process;

import javax.faces.context.FacesContext;

import org.hibernate.ce.auction.dao.ItemDAO;
import org.hibernate.ce.auction.model.Item;
import org.hibernate.ce.auction.persistence.HibernateUtil;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
public class SubmitHandler implements ActionHandler
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -4152746327411917114L;

   public void execute(ExecutionContext executionContext) throws Exception
   {
      // Get the item and save it
      JsfItemManager jsfItemManager = (JsfItemManager)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("jsfItemManager");
      Item item = jsfItemManager.getItem();
      item.setPendingForApproval();
      ItemDAO itemDao = new ItemDAO();
      itemDao.makePersistent(item);
      HibernateUtil.commitTransaction();
      HibernateUtil.closeSession();
   }

}



