
package org.jboss.seam.example.wicket;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.example.wicket.action.HotelBooking;
import org.jboss.seam.wicket.SeamLink;

@Restrict
public class Hotel extends WebPage 
{

   private org.jboss.seam.example.wicket.action.Hotel hotel;
   
   @In(create=true)
   private HotelBooking hotelBooking;

	public Hotel(final PageParameters parameters)
	{
	   super(parameters);
	   Template body = new Template("body");
	   body.add(new SeamLink("bookHotel")
	   {
	      @Override
	      public void onClick()
	      {
	         hotelBooking.bookHotel();
	         setResponsePage(Book.class);
	      }
	   });
	   body.add(new SeamLink("cancel")
      {
         @Override
         public void onClick()
         {
            hotelBooking.cancel();
            setResponsePage(Main.class);
         }
         
      });
	   initHotel();
	   body.add(new HotelViewPanel("hotel", hotel));
	   add(body);
	}
	
	@Override
	protected void onBeforeRender()
	{
	   initHotel();
	   super.onBeforeRender();
	}
	
	private void initHotel()
	{
	   if (hotel == null)
      {
         hotel = hotelBooking.selectHotel(getPageParameters().getLong("hotelId"));
      }
	}
	
	
}
