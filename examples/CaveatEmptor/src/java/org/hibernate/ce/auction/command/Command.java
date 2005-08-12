package org.hibernate.ce.auction.command;

import java.io.Serializable;

/**
 * The interface for generic commands between presentation and business tier.
 *
 * @author Christian Bauer <christian@hibernate.org>
 */
public interface Command extends Serializable {
	public void execute() throws CommandException;
}
