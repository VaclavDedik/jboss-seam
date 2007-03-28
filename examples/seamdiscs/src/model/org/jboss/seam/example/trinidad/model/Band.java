package org.jboss.seam.example.trinidad.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Band extends Artist
{
   
   @OneToMany(mappedBy="band")
   private List<BandMember> bandMembers;

   public List<BandMember> getBandMembers()
   {
      return bandMembers;
   }

   public void setBandMembers(List<BandMember> bandMembers)
   {
      this.bandMembers = bandMembers;
   }
   
   

}
