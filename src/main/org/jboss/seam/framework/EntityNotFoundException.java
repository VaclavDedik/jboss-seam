package org.jboss.seam.framework;

import javax.ejb.ApplicationException;

import org.jboss.seam.annotations.HttpError;

@HttpError(errorCode=404)
@ApplicationException(rollback=true) 
public class EntityNotFoundException extends RuntimeException
{
}
