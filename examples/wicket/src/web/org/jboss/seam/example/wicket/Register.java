package org.jboss.seam.example.wicket;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualInputValidator;
import org.apache.wicket.markup.html.link.PageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.jboss.seam.annotations.In;
import org.jboss.seam.example.wicket.action.User;

public class Register extends WebPage
{
	
	@In(create=true)
	private User user;
	
	@In(create=true)
	private org.jboss.seam.example.wicket.action.Register register;
	
	private TextField username;
	
	
	public Register()
    {
       add(new RegisterForm("registration"));
    }

	public class RegisterForm extends Form
	{

      public RegisterForm(String id)
      {
         super(id);
         add(new PageLink("cancel", Home.class));
         username = new TextField("username");
         username.setRequired(true);
         add(new FormInputBorder("usernameDecorate", "Username", username, new PropertyModel(user, "username")));
         add(new FormInputBorder("nameDecorate", "Real Name", new TextField("name").setRequired(true), new PropertyModel(user, "name")));
         FormComponent password = new PasswordTextField("password").setRequired(true);
         FormComponent verify = new PasswordTextField("verify").setRequired(true);
         add(new FormInputBorder("passwordDecorate", "Password", password , new PropertyModel(user, "password")));
         add(new FormInputBorder("verifyDecorate", "Verify Password", verify, new PropertyModel(register, "verify")));
         add(new FeedbackPanel("messages"));
         add(new EqualInputValidator(password, verify));
      }
      
      @Override
      protected void onSubmit()
      {
         register.register();
         setResponsePage(Home.class);
      }
      
      @Override
      protected void onError()
      {
         super.onError();
         System.out.println("onError");
         System.out.println(username.getFeedbackMessage());
      }
      
	}
	
	@Override
	protected void onBeforeRender()
	{
	   super.onBeforeRender();
	   System.out.println("onBeforeRender");
      System.out.println(username.getFeedbackMessage());
	}
	
}
