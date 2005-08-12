@TypeDefs({
	@TypeDef(
		name="monetary_amount_usd",
		typeClass = MonetaryAmountType.class,
		parameters = { @Parameter(name="convertTo", value="USD") }
	),
	@TypeDef(
		name="monetary_amount_eur",
		typeClass = MonetaryAmountType.class,
		parameters = { @Parameter(name="convertTo", value="EUR") }
	),
	@TypeDef(
		name="item_state",
		typeClass = EnumUserType.class,
		parameters = { @Parameter(name="enumClassName", value="org.hibernate.ce.auction.entities.ItemState") }
	),

    @TypeDef(
        name="creditcard_type",
        typeClass = EnumUserType.class,
        parameters = { @Parameter(name="enumClassName", value="org.hibernate.ce.auction.entities.CreditCardType") }
    ),
    @TypeDef(
        name="rating",
        typeClass = EnumUserType.class,
        parameters = { @Parameter(name="enumClassName", value="org.hibernate.ce.auction.entities.Rating") }
    )
})

@NamedQueries({
    @NamedQuery(
        name="item.minBid",
        queryString="select b from Bid b" +
                    "where b.amount.value = (" +
                        "select min(b.amount.value) from Bid b where b.item.id = :itemid" +
                    ")"
    ),
    @NamedQuery(
        name="item.maxBid",
        queryString="select b from Bid b" +
                    "where b.amount.value = (" +
                        "select max(b.amount.value) from Bid b where b.item.id = :itemid" +
                    ")"
    )
})

package org.hibernate.ce.auction.entities;

import org.hibernate.ce.auction.persistence.*;

import org.hibernate.annotations.*;
import javax.persistence.*;
