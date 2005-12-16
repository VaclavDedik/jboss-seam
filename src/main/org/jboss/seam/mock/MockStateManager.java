package org.jboss.seam.mock;

import java.io.IOException;

import javax.faces.application.StateManager;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

public class MockStateManager extends StateManager {

   @Override
   public SerializedView saveSerializedView(FacesContext arg0) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   protected Object getTreeStructureToSave(FacesContext arg0) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   protected Object getComponentStateToSave(FacesContext arg0) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void writeState(FacesContext arg0, SerializedView arg1)
         throws IOException {
      // TODO Auto-generated method stub

   }

   @Override
   public UIViewRoot restoreView(FacesContext arg0, String arg1, String arg2) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   protected UIViewRoot restoreTreeStructure(FacesContext arg0, String arg1,
         String arg2) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   protected void restoreComponentState(FacesContext arg0, UIViewRoot arg1,
         String arg2) {
      // TODO Auto-generated method stub

   }

}
