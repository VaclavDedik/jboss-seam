package org.jboss.seam.test.integration;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;

@Name("entityExceptionObserver")
public class EntityExceptionObserver {
    
    private boolean exceptionSeen;

    @Observer("org.jboss.seam.exceptionHandled.javax.persistence.OptimisticLockException")
    public void handleException(Exception e) {
        exceptionSeen=true;
    }
    
    public boolean getOptimisticLockExceptionSeen() {
        return exceptionSeen;
    }
}
