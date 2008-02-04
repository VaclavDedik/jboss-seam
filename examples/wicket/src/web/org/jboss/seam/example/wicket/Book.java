/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.example.wicket;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.ValidationError;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.example.wicket.action.Booking;
import org.jboss.seam.example.wicket.action.HotelBooking;

@Restrict
public class Book extends WebPage 
{
   
   private static final List<String> bedOptions = Arrays.asList("One king-sized bed", "Two double beds", "Three beds");
   private static final List<String> smokingOptions = Arrays.asList("Smoking", "Non Smoking");
   private static final List<String> monthOptions = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
   private static final List<String> yearOptions = Arrays.asList("2008", "2009");
   
   @In
   private Booking booking;
   
   @In(create=true)
   private HotelBooking hotelBooking;

	public Book(final PageParameters parameters)
	{
	   super(parameters);
	   System.out.println(parameters);
	   Template body = new Template("body");
	   add(body);
	   body.add(new FeedbackPanel("messages"));
	   body.add(new HotelViewPanel("hotelView", booking.getHotel()));
	   body.add(new HotelBookingForm("booking"));
	   
	}
	
	public class HotelBookingForm extends Form
	{
	   
      public HotelBookingForm(String id)
      {
         super(id);
         add(new FormInputBorder("checkinDateBorder", "Check in date", new DateField("checkinDate").setRequired(true), new PropertyModel(booking, "checkinDate")));
         add(new FormInputBorder("checkoutDateBorder", "Check out date", new DateField("checkoutDate").setRequired(true), new PropertyModel(booking, "checkoutDate")));
         add(new FormInputBorder("bedsBorder", "Room Preference", new DropDownChoice("beds", bedOptions)
         {
            @Override
            protected Object convertChoiceIdToChoice(String id)
            {
               return bedOptions.indexOf(id);
            }
            
         }.setRequired(true), new PropertyModel(booking, "beds")));
         add(new FormInputBorder("smokingBorder", "Smoking Preference", new RadioChoice("smoking", smokingOptions)
         {
            
            @Override
            protected Object convertChoiceIdToChoice(String id)
            {
               if ("Smoking".equals(id))
               {
                  return true;
               }
               else
               {
                  return false;
               }
            }
            
         }.setRequired(true), new PropertyModel(booking, "smoking")));
         add(new FormInputBorder("creditCardBorder", "Credit Card #", new TextField("creditCard").setRequired(true), new PropertyModel(booking, "creditCard")));
         add(new FormInputBorder("creditCardNameBorder", "Credit Card Name", new TextField("creditCardName").setRequired(true), new PropertyModel(booking, "creditCardName")));
         add(new FormInputBorder("creditCardExpiryBorder", "Credit Card Expiry", new DropDownChoice("creditCardExpiryMonth", monthOptions).setRequired(true), new PropertyModel(booking, "creditCardExpiryMonth")).add(new DropDownChoice("creditCardExpiryYear", yearOptions).setRequired(true), new PropertyModel(booking, "creditCardExpiryYear")));
         add(new Link("cancel")
         {

            @Override
            public void onClick()
            {
               setResponsePage(Main.class);
            }
            
         });          
      }
      
      
      
      @Override
      protected void onSubmit()
      {
         hotelBooking.setBookingDetails();
         if (hotelBooking.isBookingValid())
         {
            setResponsePage(Confirm.class);
         }
         else
         {
            error(new ValidationError().setMessage("Error"));
         }
      }
      
      @Override
      public Component add(IBehavior behavior)
      {
         // TODO Auto-generated method stub
         return super.add(behavior);
      }
	   
	}	
}
