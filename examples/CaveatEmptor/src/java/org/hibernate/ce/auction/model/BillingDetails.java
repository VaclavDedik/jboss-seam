package org.hibernate.ce.auction.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * This is the abstract superclass for BillingDetails.
 * <p>
 * A BillingDetails object is always associated with a single
 * User and depends on the lifecycle of that user. It represents
 * one of the billing strategies the User has choosen, usually
 * one BillingDetails is the default in a collection of many.
 *
 * @author Christian Bauer <christian@hibernate.org>
 */
@Entity(access = AccessType.FIELD)
@Table(name = "BILLING_DETAILS")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BillingDetails implements Serializable, Comparable {

    @Id(generate = GeneratorType.AUTO)
	@Column(name = "BILLING_DETAILS_ID")
	private Long id = null;

    @Version
    private int version = 0;

    @Column(name = "OWNER_NAME", nullable = false)
	private String ownerName;

    @ManyToOne
    @JoinColumn(name = "USER_ID", updatable = false)
	private User user;

    @Column( nullable = false, updatable = false)
	private Date created = new Date();

	/**
	 * No-arg constructor for JavaBean tools.
	 */
	BillingDetails() {}

	/**
	 * Full constructor;
	 */
	protected BillingDetails(String ownerName, User user) {
		this.ownerName = ownerName;
		this.user = user;
	}

	// ********************** Accessor Methods ********************** //

	public Long getId() { return id; }
    public int getVersion() { return version; }

	public String getOwnerName() { return ownerName; }
	public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

	public User getUser() { return user; }

	public Date getCreated() { return created; }

	// ********************** Common Methods ********************** //

	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BillingDetails)) return false;

		final BillingDetails billingDetails = (BillingDetails) o;

		if (!getCreated().equals(billingDetails.getCreated())) return false;
		if (!getOwnerName().equals(billingDetails.getOwnerName())) return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = getCreated().hashCode();
		result = 29 * result + getOwnerName().hashCode();
		return result;
	}

	public int compareTo(Object o) {
		// Billing Details are simply sorted by creation date
		if (o instanceof BillingDetails)
			return getCreated().compareTo( ((BillingDetails)o).getCreated() );
		return 0;
	}

	// ********************** Business Methods ********************** //

	/**
	 * Checks if the billing information is correct.
	 * <p>
	 * Check algorithm is implemented in subclasses.
	 *
	 * @return boolean
	 */
	public abstract boolean isValid();

}
