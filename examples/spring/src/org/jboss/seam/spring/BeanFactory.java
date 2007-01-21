package org.jboss.seam.spring;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.log.Log;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class BeanFactory {
    @Logger Log log;
    
    org.springframework.beans.factory.BeanFactory factory;
    
    String beanFile = "spring-beans.xml";
    
    public void setBeanFile(String beanFile) {
        this.beanFile = beanFile;
    }
    
    @Create
    public void createBeanFactory() {
        factory = new XmlBeanFactory(new ClassPathResource(beanFile));
        log.info("Created bean factory for #0", beanFile);
    }
    
    @Unwrap
    public org.springframework.beans.factory.BeanFactory getFactory() {
        return factory;
    }
}
