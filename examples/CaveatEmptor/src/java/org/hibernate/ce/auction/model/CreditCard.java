package org.hibernate.ce.auction.model;

import javax.persistence.*;

/**
 * This billing strategy can handle various credit cards.
 * <p>
 * The type of credit card is handled with a typesafe
 * enumeration, <tt>CreditCardType</tt>.
 *
 * @see CreditCardType
 * @author Christian Bauer <christian@hibernate.org>
 */
@Entity(access = AccessType.FIELD)
@Table(name = "CREDIT_CARD")
@Inheritance(strategy = InheritanceType.JOINED)
@JoinColumn(name = "CREDIT_CARD_ID")
public class CreditCard extends BillingDetails {

    //TODO: Test if this really works
    @org.hibernate.annotations.Type(type = "creditcard_type")
    @Column(name = "CC_TYPE", nullable = false)
	private CreditCardType type;

    @Column(name = "CC_NUMBER", nullable = false, updatable = false)
	private String number;

    @Column(name = "EXP_MONTH", nullable = false, updatable = false)
	private String expMonth;

    @Column(name = "EXP_YEAR", nullable = false, updatable = false)
	private String expYear;

	/**
	 * No-arg constructor for JavaBean tools.
	 */
	CreditCard() { super(); }

	/**
	 * Full constructor.
	 *
	 * @param ownerName
	 * @param user
	 * @param type
	 * @param expMonth
	 * @param expYear
	 */
	public CreditCard(String ownerName, User user, String number, CreditCardType type,
					  String expMonth, String expYear) {
		super(ownerName, user);
		this.type = type;
		this.number = number;
		this.expMonth = expMonth;
		this.expYear = expYear;
	}

	// ********************** Accessor Methods ********************** //

	public CreditCardType getType() { return type; }

	public String getNumber() { return number; }

	public String getExpMonth() { return expMonth; }

	public String getExpYear() { return expYear; }

	// ********************** Common Methods ********************** //

	public String toString() {
		return  "CreditCard ('" + getId() + "'), " +
				"Type: '" + getType() + "'";
	}

	// ********************** Business Methods ********************** //

	public boolean isValid() {
		// Use the type to validate the CreditCard details.
		return getType().isValid(this);
	}

}
