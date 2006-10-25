package org.jboss.seam.jsf;

import java.io.IOException;

import javax.faces.application.StateManager;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.jboss.seam.core.Pages;

/**
 * A wrapper for the JSF implementation's StateManager that allows
 * us to intercept saving of the serialized component tree. This
 * is quite ugly but was needed in order to allow conversations to
 * be started and manipulated during the RENDER_RESPONSE phase.
 * 
 * @author Gavin King
 */
public class SeamStateManager extends StateManager {
   private final StateManager stateManager;

   public SeamStateManager(StateManager sm) {
      this.stateManager = sm;
   }

   @Override
   protected Object getComponentStateToSave(FacesContext ctx) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected Object getTreeStructureToSave(FacesContext ctx) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected void restoreComponentState(FacesContext ctx, UIViewRoot viewRoot, String str) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected UIViewRoot restoreTreeStructure(FacesContext ctx, String str1, String str2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public UIViewRoot restoreView(FacesContext ctx, String str1, String str2) {
      return stateManager.restoreView(ctx, str1, str2);
   }

   @Override
   public SerializedView saveSerializedView(FacesContext facesContext) {
      Pages.instance().storePageParameters(facesContext);
      AbstractSeamPhaseListener.storeAnyConversationContext(facesContext);
      return stateManager.saveSerializedView(facesContext);
   }

   @Override
   public void writeState(FacesContext ctx, SerializedView sv) throws IOException {
      stateManager.writeState(ctx, sv);
   }

   @Override
   public boolean isSavingStateInClient(FacesContext ctx) {
      return stateManager.isSavingStateInClient(ctx);
   }
}