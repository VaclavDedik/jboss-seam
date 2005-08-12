package org.hibernate.ce.auction.command;

import org.hibernate.ce.auction.persistence.HibernateUtil;


/**
 * The implementation of a generic EJB command handler.
 *
 * @author Christian Bauer <christian@hibernate.org>
 */
public class CommandHandlerBean {

	public Command executeCommand(Command command)
		throws CommandException {

		try {
			command.execute();
		} catch (CommandException ex) {
			// Actually, set the UserTransaction in JTA to rollback only.
			// It is possible to not catch the exception and let the
			// container set rollback when this method fails.
			HibernateUtil.rollbackTransaction();
			throw ex;
		}
		return command;
	}
}
