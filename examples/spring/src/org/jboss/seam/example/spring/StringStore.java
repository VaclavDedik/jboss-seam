package org.jboss.seam.example.spring;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.springframework.beans.factory.BeanNameAware;

/**
 * @author youngm
 */
public class StringStore 
    implements BeanNameAware, 
               Serializable 
{
    private String beanName = "pageSeamStringStore";
    private List<String> strings = new ArrayList<String>();

    public void addString(String string) {
        strings.add(string);
    }

    public List<String> getStrings() {
        return new ArrayList<String>(strings);
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
