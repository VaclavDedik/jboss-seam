package org.jboss.seam.core;

import java.lang.reflect.Method;

import org.jboss.el.ExpressionFactoryImpl;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Reflections;

@Name("org.jboss.seam.core.jbossELInstaller")
@Startup
@Scope(ScopeType.APPLICATION)
public class JBossELInstaller {        
    //private static final String TOMCAT_APPLICATION_CONTEXT = "org.apache.jasper.runtime.JspApplicationContextImpl";
    private static final String JSF_RI_ASSOCIATE = "com.sun.faces.ApplicationAssociate";
    
    private static final LogProvider log = Logging.getLogProvider(JBossELInstaller.class);

    @Create
    public void installJBossEL() {                   
//        configureTomcatApplicationContext();
        configureRIAssociate();
    }

    /**
     * Set the default expression factory for the JSF RI     
     */
    public void configureRIAssociate() {        
        Object target = Contexts.getApplicationContext().get(JSF_RI_ASSOCIATE);
        if (target != null) {
            Method method = Reflections.getSetterMethod(target.getClass(), "expressionFactory");
            try {
                method.invoke(target, new Object[] {new ExpressionFactoryImpl()});  
                log.debug("set expression factory on RI associate");
            } catch (Exception e) {
                log.error("couldn't set expression factory on RI associate", e);
            }
        }
    }

//     /**
//      * Set the expression factory for the tomcat JSPApplicationContext associated with this deployment.
//      * This works, but the use EL enhancements in JSPs will be rejected by tomcat as invalid.  
//      */
//     public void configureTomcatApplicationContext() {
//         Context appContext = Contexts.getApplicationContext();
//         JspApplicationContext target = (JspApplicationContext)appContext.get(TOMCAT_APPLICATION_CONTEXT);
//         if (target != null) {
//             ProxyFactory factory = new ProxyFactory();
//             factory.setSuperclass( target.getClass());
//             factory.setFilter(new MethodFilter() {
//                 public boolean isHandled(Method method) {
//                     return method.getName().equals("getExpressionFactory");
//                 }                
//             });
//             factory.setHandler(new MethodHandler() {
//                 public Object invoke(Object arg0, Method arg1, Method arg2, Object[] arg3) throws Throwable {
//                     return new ExpressionFactoryImpl();
//                 }
//             });

//             try {
//                 target = (JspApplicationContext) factory.create(new Class[0], new Object[0]);

//                 appContext.set(TOMCAT_APPLICATION_CONTEXT, target);
//                 log.debug("replaced tomcat application context");
//             } catch (Exception e ) {
//                 log.error("couldn't replace tomcat application context", e);
//             }
//         }
//     }

}
