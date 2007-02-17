package org.jboss.seam.example.spring;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.springframework.beans.factory.BeanNameAware;

/**
 * @author youngm
 */
public class StringStore 
    implements BeanNameAware, 
               Serializable 
{
    private String beanName;
    private List<String> strings = new ArrayList<String>();

    @In(value="pageSeamStringStore", create=true)
    private StringStore someOther;
    
    public void init() {
        System.out.println("Initializing: "+beanName);
    }
    
    public void addString(String string) {
        strings.add(string);
    }
    
    public List<String> getStrings() {
        return new ArrayList<String>(strings);
    }
    
    @Destroy
    public void destory() {
        System.out.println("Called Destroy "+beanName);
    }
    
    @Create
    public void create() {
        System.out.println("Called Create: "+beanName);
    }
    
    /**
     * @return the name
     */
    public String getBeanName() {
        return beanName;
    }
    
    /**
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
