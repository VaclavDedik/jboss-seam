package org.jboss.seam.ioc.spring;

import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.w3c.dom.Element;

/**
 * @author youngm
 */
public class SeamNamespaceHandler 
    extends NamespaceHandlerSupport
{
    /**
     * @see org.springframework.beans.factory.xml.NamespaceHandler#init()
     */
    public void init()
    {
        registerBeanDefinitionParser("instance", new SeamBeanBeanDefinitionParser());
    }
    
    private static class SeamBeanBeanDefinitionParser 
        extends AbstractSimpleBeanDefinitionParser
    {
        
        protected Class getBeanClass(Element element)
        {
            return SeamFactoryBean.class;
        }
        
        /*              
        protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
            String id = super.resolveId(element, definition, parserContext);
            return id;
        }
       */
    }
}
