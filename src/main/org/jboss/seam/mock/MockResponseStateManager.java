package org.jboss.seam.mock;

import java.io.IOException;

import javax.faces.application.StateManager.SerializedView;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;

public class MockResponseStateManager extends ResponseStateManager {

   @Override
   public Object getComponentStateToRestore(FacesContext arg0) {
      return new Object();
   }

   @Override
   public Object getTreeStructureToRestore(FacesContext arg0, String arg1) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void writeState(FacesContext arg0, SerializedView arg1) throws IOException {
      // TODO Auto-generated method stub
      
   }

}
