package com.jboss.dvd.seam.test;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;
import org.jboss.seam.mock.SeamTest;

public class BaseTest 
    extends SeamTest
{

//     public Object getInstance(String name, boolean create) {
//         return Component.getInstance(name,create);
//     }

    public Object lookup(String name) {
        return Contexts.lookupInStatefulContexts(name);
    }
    
    public boolean inConversation() {
        return Manager.instance().isLongRunningConversation();
    }
}
