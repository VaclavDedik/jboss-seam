package org.hibernate.ce.auction.command;

/**
 * A checked exception thrown by command execute methods, wrapping the root cause.
 *
 * @author Christian Bauer <christian@hibernate.org>
 */
public class CommandException
	extends Exception {

	public CommandException() {}

	public CommandException(String message) {
		super(message);
	}

	public CommandException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommandException(Throwable cause) {
		super(cause);
	}
}
