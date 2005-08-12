package org.hibernate.ce.auction.model;

public enum CreditCardType {

	MASTERCARD(1, "Mastercard"),
    VISA(2, "Visa"),
    AMEX(3, "American Express");

    private final int code;
    private final String debugName;

	private CreditCardType(int code, String debugName) {
		this.debugName = debugName;
		this.code = code;
	}

	public String toString() {
	    return debugName;
	}

    public int value() {
        return code;
    }

	// ********************** Business Methods ********************** //

	public boolean isValid(CreditCard cc) {
		// TODO: Implement syntactical validation of credit card information.
		return true;
	}

}