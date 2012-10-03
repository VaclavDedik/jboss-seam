package org.jboss.seam.ui.component;

/**
 * Component that is aware of being used from a facet by s:decorate
 */
public interface UIDecorateAware 
{
    /**
    * Method called by DecorateRendererBase when it's about to render a subtree containing this component.
    */
    void setUIDecorate(UIDecorate decorate);
}
