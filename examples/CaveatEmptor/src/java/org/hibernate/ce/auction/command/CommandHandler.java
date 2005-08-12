package org.hibernate.ce.auction.command;


/**
 * A generic handler for EJB commands.
 *
 * @author Christian Bauer <christian@hibernate.org>
 */
public interface CommandHandler {

	public Command executeCommand(Command command)
		throws CommandException;

}
