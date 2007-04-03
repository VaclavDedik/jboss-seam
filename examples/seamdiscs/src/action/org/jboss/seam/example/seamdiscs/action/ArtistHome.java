package org.jboss.seam.example.seamdiscs.action;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.seamdiscs.model.Artist;
import org.jboss.seam.example.seamdiscs.model.Band;
import org.jboss.seam.example.seamdiscs.model.BandMember;
import org.jboss.seam.example.seamdiscs.model.Disc;
import org.jboss.seam.framework.EntityHome;

@Name("artistHome")
public class ArtistHome extends EntityHome<Artist>
{

   @Factory
   public Artist getArtist()
   {
      return super.getInstance();
   }
   
   private String type;
   
   public String getType()
   {
      return type;
   }
   
   public void setType(String type)
   {
      this.type = type;
   }
   
   @Override
   protected Artist createInstance()
   {
      if (Band.class.getSimpleName().equalsIgnoreCase(getType()))
      {
         return new Band();
      }
      else
      {
         return new Artist();
      }
   }
   
   public void addBandMember()
   {
      Band band = (Band) getInstance();
      band.getBandMembers().add(new BandMember(band));
   }
   
   public void addDisc()
   {
      getInstance().getDiscs().add(new Disc(getInstance()));
   }
}
