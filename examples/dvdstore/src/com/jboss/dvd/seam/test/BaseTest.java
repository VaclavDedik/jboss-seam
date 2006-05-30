package com.jboss.dvd.seam.test;

import java.util.*;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Ejb;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.mock.SeamTest;

public class BaseTest 
    extends SeamTest
{

    @Override
    public void initServletContext(Map initParams)
    {
        initParams.put(Init.COMPONENT_CLASSES, Ejb.class.getName());
        initParams.put(Init.JNDI_PATTERN, "#{ejbName}/local");
    }

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
