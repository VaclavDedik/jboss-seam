//$Id: Hotel.groovy 4698 2007-04-18 06:40:06Z ebernard $
package org.jboss.seam.example.groovy.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

import org.jboss.seam.annotations.Name
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size


@Entity
@Name("hotel")
class Hotel implements Serializable
{
   @Id @GeneratedValue
   Long id

   @Size(max=50) @NotNull
   String name

   @Size(max=100) @NotNull
   String address

   @Size(max=40) @NotNull
   String city

   @Size(min=2, max=10) @NotNull
   String state

   @Size(min=4, max=6) @NotNull
   String zip

   @Size(min=2, max=40) @NotNull
   String country

   @Column(precision=6, scale=2)
   BigDecimal price

   @Override
   String toString()
   {
      return "Hotel(${name},${address},${city},${zip})"
   }
}
