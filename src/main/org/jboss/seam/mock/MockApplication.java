package org.jboss.seam.mock;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.validator.Validator;

public class MockApplication extends Application {

   @Override
   public ActionListener getActionListener() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setActionListener(ActionListener arg0) {
      // TODO Auto-generated method stub

   }

   @Override
   public Locale getDefaultLocale() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setDefaultLocale(Locale arg0) {
      // TODO Auto-generated method stub

   }

   @Override
   public String getDefaultRenderKitId() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setDefaultRenderKitId(String arg0) {
      // TODO Auto-generated method stub

   }

   @Override
   public String getMessageBundle() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setMessageBundle(String arg0) {
      // TODO Auto-generated method stub

   }

   @Override
   public NavigationHandler getNavigationHandler() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setNavigationHandler(NavigationHandler arg0) {
      // TODO Auto-generated method stub

   }

   @Override
   public PropertyResolver getPropertyResolver() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setPropertyResolver(PropertyResolver arg0) {
      // TODO Auto-generated method stub

   }

   @Override
   public VariableResolver getVariableResolver() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setVariableResolver(VariableResolver arg0) {
      // TODO Auto-generated method stub

   }
   
   private ViewHandler viewHandler = new MockViewHandler();

   @Override
   public ViewHandler getViewHandler() {
      return viewHandler;
   }

   @Override
   public void setViewHandler(ViewHandler viewHandler) {
      this.viewHandler = viewHandler;
   }
   
   private StateManager stateManager = new MockStateManager();

   @Override
   public StateManager getStateManager() {
      return stateManager;
   }

   @Override
   public void setStateManager(StateManager stateManager) {
      this.stateManager = stateManager;
   }

   @Override
   public void addComponent(String arg0, String arg1) {
      // TODO Auto-generated method stub

   }

   @Override
   public UIComponent createComponent(String arg0) throws FacesException {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public UIComponent createComponent(ValueBinding arg0, FacesContext arg1,
         String arg2) throws FacesException {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Iterator getComponentTypes() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void addConverter(String arg0, String arg1) {
      // TODO Auto-generated method stub

   }

   @Override
   public void addConverter(Class arg0, String arg1) {
      // TODO Auto-generated method stub

   }

   @Override
   public Converter createConverter(String arg0) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Converter createConverter(Class arg0) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Iterator getConverterIds() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Iterator getConverterTypes() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public MethodBinding createMethodBinding(String arg0, Class[] arg1)
         throws ReferenceSyntaxException {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Iterator getSupportedLocales() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setSupportedLocales(Collection arg0) {
      // TODO Auto-generated method stub

   }

   @Override
   public void addValidator(String arg0, String arg1) {
      // TODO Auto-generated method stub

   }

   @Override
   public Validator createValidator(String arg0) throws FacesException {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Iterator getValidatorIds() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ValueBinding createValueBinding(final String valueExpression)
         throws ReferenceSyntaxException {
      return new ValueBinding() {

		@Override
		public Class getType(FacesContext ctx) throws EvaluationException, PropertyNotFoundException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getValue(FacesContext ctx) throws EvaluationException, PropertyNotFoundException {
			return valueExpression;
		}

		@Override
		public boolean isReadOnly(FacesContext arg0) throws EvaluationException, PropertyNotFoundException {
			return false;
		}

		@Override
		public void setValue(FacesContext arg0, Object arg1) throws EvaluationException, PropertyNotFoundException {
		}
    	  
      };
   }

}
