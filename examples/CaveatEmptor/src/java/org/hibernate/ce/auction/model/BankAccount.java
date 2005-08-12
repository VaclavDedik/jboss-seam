package org.hibernate.ce.auction.model;

import javax.persistence.*;

/**
 * This billing strategy uses a simple bank account.
 *
 * @author Christian Bauer <christian@hibernate.org>
 */
@Entity(access = AccessType.FIELD)
@Table(name = "BANK_ACCOUNT")
@JoinColumn(name = "BANK_ACCOUNT_ID")
public class BankAccount extends BillingDetails {

    @Column(name = "BA_NUMBER", nullable = false, updatable = false)
	private String number;

    @Column(name = "BA_NAME", nullable = false, updatable = false)
	private String bankName;

    @Column(name = "BANK_SWIFT", nullable = false, updatable = false)
	private String bankSwift;

	/**
	 * No-arg constructor for JavaBean tools.
	 */
	BankAccount() { super(); }

	/**
	 * Full constructor.
	 *
	 * @param ownerName
	 * @param user
	 * @param number
	 * @param bankName
	 * @param bankSwift
	 */
	public BankAccount(String ownerName, User user, String number, String bankName, String bankSwift) {
		super(ownerName, user);
		this.number = number;
		this.bankName = bankName;
		this.bankSwift = bankSwift;
	}

	// ********************** Accessor Methods ********************** //

	public String getNumber() { return number; }
	public void setNumber(String number) { this.number = number; }

	public String getBankName() { return bankName; }
	public void setBankName(String bankName) { this.bankName = bankName; }

	public String getBankSwift() { return bankSwift; }
	public void setBankSwift(String bankSwift) { this.bankSwift = bankSwift; }

	// ********************** Common Methods ********************** //

	public String toString() {
		return  "BankAccount ('" + getId() + "'), " +
				"Number: '" + getNumber() + "'";
	}

	// ********************** Business Methods ********************** //

	public boolean isValid() {
		// TODO: Validate bank account syntax.
		return true;
	}

}
