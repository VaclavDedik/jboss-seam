package org.hibernate.ce.auction.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * A single item in a single category, with additional information.
 * <p>
 * This is really a very special mapping. The CategorizedItem class
 * represents an association table. The ER model for this is really
 * a many-to-many association, but instead of two entities and two
 * collections, we mapped this as two one-to-many associations between
 * three entities. One of the motivation for this are the additional
 * attributes on the association table (not only two FKs): username
 * and creation date.
 *
 * @see org.hibernate.ce.auction.model.Category
 * @see org.hibernate.ce.auction.model.Item
 * @author Christian Bauer <christian@hibernate.org>
 */
@Entity(access = AccessType.FIELD)
@Table(name = "CATEGORIZED_ITEM")
public class CategorizedItem implements Serializable, Comparable {

    @Embeddable(access = AccessType.FIELD)
	public static class Id implements Serializable {

        @Column(name = "CAT_ID")
		private Long categoryId;
        @Column(name = "ITEM_ID")
		private Long itemId;

		public Id() {}

		public Id(Long categoryId, Long itemId) {
			this.categoryId = categoryId;
			this.itemId = itemId;
		}

		public boolean equals(Object o) {
			if (o instanceof Id) {
				Id that = (Id)o;
				return this.categoryId.equals(that.categoryId) &&
					   this.itemId.equals(that.itemId);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return categoryId.hashCode() + itemId.hashCode();
		}
	}

    @EmbeddedId
	private Id id = new Id();

    @Column(name = "ADDED_BY_USER")
	private String username; // This could also be an association to User

    @Column(name = "ADDED_ON")
	private Date dateAdded = new Date();

    @ManyToOne
    @JoinColumn(name="ITEM_ID", insertable = false, updatable = false)
	private Item item;

    @ManyToOne
    @JoinColumn(name="CAT_ID", insertable = false, updatable = false)
	private Category category;

	/**
	 * No-arg constructor for JavaBean tools.
	 */
    CategorizedItem() {}

	/**
	 * Full constructor;
	 */
	public CategorizedItem(String username, Category category, Item item) {
		this.username = username;

		this.category = category;
		this.item = item;

		// Set key values
		this.id.categoryId = category.getId();
		this.id.itemId = item.getId();

		// Guarantee referential integrity
		category.getCategorizedItems().add(this);
		item.getCategorizedItems().add(this);
	}

	// ********************** Accessor Methods ********************** //

	public Id getId() { return id; }

	public String getUsername() { return username; }
	public Date getDateAdded() { return dateAdded; }

	public Category getCategory() { return category; }
	public Item getItem() { return item; }

	// ********************** Common Methods ********************** //

	public int compareTo(Object o) {
		// CategorizedItems are sorted by date
		if (o instanceof CategorizedItem)
			return getDateAdded().compareTo( ((CategorizedItem)o).getDateAdded() );
		return 0;
	}

	public String toString() {
		return  "Added by: '" + getUsername() + "', " +
				"On Date: '" + getDateAdded();
	}

	// ********************** Business Methods ********************** //

}