package org.jboss.seam.example.wicket;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.jboss.seam.wicket.ModelValidator;

/**
 * Wicket allows you to build powerful custom components easily.
 * 
 * Here we've built generic border you can use to decorate a form input with 
 * a label, a * if the field is required and an feedback panel for displaying
 * any error messages.
 * 
 * It also attaches a model validator (which asks Seam to validate the input against
 * Hibernate Validator).
 * based
 * 
 * @author Pete Muir
 *
 */
public class FormInputBorder extends Border
{

   private ComponentFeedbackPanel feedbackPanel;
   
   /**
    * Create a new form input border
    * @param id Id of border component on page
    * @param label Label to add
    * @param component The component to wrap
    * @param model The model to attach the component to
    */
   public FormInputBorder(String id, String label, FormComponent component, PropertyModel model)
   {
      super(id);
      component.setLabel(new Model(label));
      if (component.isRequired())
      {
         label += ":*";
      }
      else
      {
         label += ":";
      }
      Label labelComponent = new Label("label", label);
      add(labelComponent);
      add(component, model);
      feedbackPanel = new ComponentFeedbackPanel("message", component);
      feedbackPanel.setOutputMarkupId(true);
      add(feedbackPanel);
      component.add(new ModelValidator(model));
      
      component.add(new AjaxFormComponentUpdatingBehavior("onblur")
      {

         @Override
         protected void onUpdate(AjaxRequestTarget target)
         {
            getFormComponent().validate();
            target.addComponent(feedbackPanel);
         }
         
         @Override
         protected void onError(AjaxRequestTarget target, RuntimeException e)
         {
            target.addComponent(feedbackPanel);
         }
         
         @Override
         protected boolean getUpdateModel()
         {
            return true;
         }
         
      });
   }
   
   public FormInputBorder add(FormComponent component, PropertyModel model)
   {
      component.add(new ModelValidator(model));
      component.setModel(model);
      add(component);
      return this;
   }

}
