/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;

import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Inject;
import org.jbpm.db.JbpmSession;
import org.jbpm.db.JbpmSessionFactory;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Interceptor for injection
 * 
 * @author Gavin King
 * @version $Revision$
 */
public class SeamInterceptor {
	
	private static final Logger log = Logger.getLogger(SeamInterceptor.class);
	
	@AroundInvoke
	public Object inject(InvocationContext invocation) throws Exception {
		Object bean = invocation.getBean();
		
		log.info( "injecting dependencies to: " + bean.getClass().getName() );
		
		injectFields( bean );
		
		injectMethods( bean );
		
		return invocation.proceed();
	}
    
	private void injectMethods(Object bean) {
		Method[] methods = bean.getClass().getDeclaredMethods();
		for ( Method method: methods ) {
			Inject inject = method.getAnnotation(Inject.class);
			if (inject!=null) {
				injectMethod( bean, method, inject );
			}
		}
	}

	private void injectMethod(Object bean, Method method, Inject inject) {
		String name = inject.value();
		if ( name.length()==0 ) {
			name = method.getName().substring(3);
		}
		
		Object value = new SeamVariableResolver().resolveVariable(name, false);
		
		try {
			log.info("injecting: " + name);
			if ( !method.isAccessible() ) method.setAccessible(true);
			method.invoke(bean, new Object[] { value } );
		}
		catch (Exception e) {
			throw new IllegalArgumentException("could not inject: " + name, e);
		}
	}

	private void injectFields(Object bean) {
		Field[] fields = bean.getClass().getDeclaredFields();
		for ( Field field: fields ) {
			Inject inject = field.getAnnotation(Inject.class);
			if (inject!=null) {
				injectField( bean, field, inject );
			}

            
            // Inject a process instance
            org.jboss.seam.annotations.ProcessInstance processInstance = field.getAnnotation(org.jboss.seam.annotations.ProcessInstance.class);
            if (processInstance != null)
            {
               injectProcessInstanceField(bean, field, processInstance);
            }

		}
	}

	private void injectField(Object bean, Field field, Inject inject) {
		String name = inject.value();
		if ( name.length()==0 ) {
			name = field.getName();
		}
		
		Object value = new SeamVariableResolver().resolveVariable(name, false);
		
		try {
			log.info("injecting: " + name);
			if ( !field.isAccessible() ) field.setAccessible(true);
			field.set(bean, value);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("could not inject: " + name, e);
		}
	}

    private void injectProcessInstanceField(Object bean, Field field, org.jboss.seam.annotations.ProcessInstance processInstanceAnnotation) {
        String name = processInstanceAnnotation.value();
        
        JbpmSessionFactory jbpmSessionFactory = JbpmSessionFactory.buildJbpmSessionFactory();

        JbpmSession jbpmSession = jbpmSessionFactory.openJbpmSession();
        jbpmSession.beginTransaction();
        ProcessInstance processInstance = null;
        try
        {
           ProcessDefinition processDefinition = jbpmSession.getGraphSession().findLatestProcessDefinition(name);
           if (processDefinition != null)
           {
              processInstance = new ProcessInstance(processDefinition);
              jbpmSession.getGraphSession().saveProcessInstance(processInstance);
           }
           else
           {
              log.warn("ProcessDefinition: " + name + " could be found");
           }
        }
        finally
        {
           jbpmSession.commitTransactionAndClose();
        }
        try {
            log.info("injecting: " + name);
            if ( !field.isAccessible() ) field.setAccessible(true);
            field.set(bean, processInstance);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("could not inject: " + name, e);
        }
    }

}
