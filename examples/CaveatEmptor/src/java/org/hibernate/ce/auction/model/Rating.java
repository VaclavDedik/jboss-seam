package org.hibernate.ce.auction.model;

public enum Rating {

	EXCELLENT("Excellent"), OK("OK"), LOW("Low");

    private String name;

	private Rating(String name) {
		this.name = name;
	}

	// ********************** Common Methods ********************** //

	public String toString() {
		return name;
	}

    public String value() {
        return name;
    }

}