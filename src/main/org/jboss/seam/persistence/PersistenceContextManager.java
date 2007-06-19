package org.jboss.seam.persistence;

import org.jboss.seam.annotations.FlushModeType;

public interface PersistenceContextManager
{
   public void changeFlushMode(FlushModeType flushMode);
}
