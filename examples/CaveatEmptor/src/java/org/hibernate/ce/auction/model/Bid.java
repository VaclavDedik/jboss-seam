package org.hibernate.ce.auction.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * An immutable class representing one bid.
 *
 * @see Item
 * @see User
 * @author Christian Bauer <christian@hibernate.org>
 */
@Entity(access = AccessType.FIELD)
@Table(name = "BID")
@org.hibernate.annotations.Entity(mutable = false)
public class Bid implements Serializable, Comparable {

    @Id(generate = GeneratorType.AUTO)
    @Column(name = "CAT_ID")
    private Long id = null;

    @org.hibernate.annotations.Type(type = "monetary_amount_usd")
    @org.hibernate.annotations.Columns( columns = {
			@Column( name="AMOUNT"),
			@Column( name="AMOUNT_CURRENCY", length = 2)
			}
	)
    private MonetaryAmount amount;

    @ManyToOne()
    @JoinColumn(name = "ITEM_ID", nullable = false)
	private Item item;

    @ManyToOne()
    @JoinColumn(name = "BIDDER_ID", nullable = false)
	private User bidder;

    @Column( nullable = false, updatable = false)
	private Date created = new Date();

	/**
	 * No-arg constructor for JavaBean tools.
	 */
	Bid() {}

	/**
	 * Full constructor.
	 *
	 * @param amount
	 * @param item
	 * @param bidder
	 */

	public Bid(MonetaryAmount amount, Item item, User bidder) {
		this.amount = amount;
		this.item = item;
		this.bidder = bidder;
	}

	// ********************** Accessor Methods ********************** //

	public Long getId() { return id; }

	public MonetaryAmount getAmount() { return amount; }
	public Item getItem() { return item; }
	public User getBidder() { return bidder; }
	public Date getCreated() { return created; }

	// ********************** Common Methods ********************** //

	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Bid)) return false;

		final Bid bid = (Bid) o;

		if (!amount.equals(bid.amount)) return false;
		if (!created.equals(bid.created)) return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = amount.hashCode();
		result = 29 * result + created.hashCode();
		return result;
	}

	public String toString() {
		return  "Bid ('" + getId() + "'), " +
				"Created: '" + getCreated() + "' " +
				"Amount: '" + getAmount() + "'";
	}

	public int compareTo(Object o) {
		if (o instanceof Bid) {
			return this.getCreated().compareTo( ((Bid)o).getCreated() );
		}
		return 0;
	}

	// ********************** Business Methods ********************** //


}