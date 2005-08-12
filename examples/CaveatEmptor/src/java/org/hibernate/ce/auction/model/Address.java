package org.hibernate.ce.auction.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The address of a User.
 *
 * An instance of this class is always associated with only
 * one <tt>User</tt> and depends on that parent objects lifecycle,
 * it is a component.
 *
 * @see User
 * @author Christian Bauer <christian@hibernate.org>
 */
@Embeddable(access = AccessType.FIELD)
public class Address implements Serializable {

	@Column(length = 255)
	private String street;

	@Column(length = 16)
	private String zipcode;

	@Column(length = 255)
	private String city;

	/**
	 * No-arg constructor for JavaBean tools.
	 */
	Address() {}

	/**
	 * Full constructor.
	 */
	public Address(String street, String zipcode, String city) {
		this.street = street;
		this.zipcode = zipcode;
		this.city = city;
	}

	// ********************** Accessor Methods ********************** //

	public String getStreet() { return street; }
	public void setStreet(String street) { this.street = street; }

	public String getZipcode() { return zipcode; }
	public void setZipcode(String zipcode) { this.zipcode = zipcode; }

	public String getCity() { return city; }
	public void setCity(String city) { this.city = city; }

	// ********************** Common Methods ********************** //

	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Address)) return false;

		final Address address = (Address) o;

		if (!city.equals(address.city)) return false;
		if (!street.equals(address.street)) return false;
		if (!zipcode.equals(address.zipcode)) return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = street.hashCode();
		result = 29 * result + zipcode.hashCode();
		result = 29 * result + city.hashCode();
		return result;
	}

	public String toString() {
		return  "Street: '" + getStreet() + "', " +
				"Zipcode: '" + getZipcode() + "', " +
				"City: '" + getCity() + "'";
	}

	// ********************** Business Methods ********************** //

}
