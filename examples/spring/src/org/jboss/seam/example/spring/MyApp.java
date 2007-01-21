package org.jboss.seam.example.spring;

import org.jboss.seam.annotations.*;

import org.springframework.beans.factory.BeanFactory;

@Name("app")
public class MyApp {
    @In(create=true)
    BeanFactory beanFactory;
    
    @In(create=true)
    BeanFactory springContext;
    
    @Out
    TestBean testBean;
    
    public void loadBeans() {                       
        testBean = (TestBean) beanFactory.getBean("testBean");
    }
    
    public void loadBeans2() {
        testBean = (TestBean) springContext.getBean("testBean1");
    }
    
}
