package org.jboss.seam.framework;

import org.jboss.seam.annotations.HttpError;

@HttpError(errorCode=404)
public class EntityNotFoundException extends RuntimeException
{
}
