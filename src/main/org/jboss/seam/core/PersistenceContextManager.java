package org.jboss.seam.core;

import org.jboss.seam.annotations.FlushModeType;

public interface PersistenceContextManager
{
   public void changeFlushMode(FlushModeType flushMode);
}
