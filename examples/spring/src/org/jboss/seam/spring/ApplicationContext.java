package org.jboss.seam.spring;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.log.Log;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApplicationContext {
    @Logger Log log;
   
    org.springframework.context.ApplicationContext context;
    
    String[] beanFiles;
    
    public void setBeanFiles(String[] beanFiles) {
        this.beanFiles = beanFiles;
    }
    
    @Create
    public void createBeanFactory() {
        context = new ClassPathXmlApplicationContext(beanFiles);
        log.info("Created spring application context");
    }
    
    @Unwrap
    public org.springframework.context.ApplicationContext getContext() {
        return context;
    }
}
