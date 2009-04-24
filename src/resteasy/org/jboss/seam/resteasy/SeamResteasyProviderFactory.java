package org.jboss.seam.resteasy;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * TODO: We need to significantly extend and change that class so we can lookup provider instances
 * through Seam at runtime. The original class has only been designed for registration of "singleton"
 * providers during startup.
 *
 * @author Christian Bauer
 */
public class SeamResteasyProviderFactory extends ResteasyProviderFactory
{

    public static void setInstance(ResteasyProviderFactory factory)
    {
        ResteasyProviderFactory.setInstance(factory);
    }

    public static ResteasyProviderFactory getInstance()
    {
        return ResteasyProviderFactory.getInstance();
    }

}
