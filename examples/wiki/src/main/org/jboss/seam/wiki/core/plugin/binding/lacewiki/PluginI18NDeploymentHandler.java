/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.plugin.binding.lacewiki;

import org.jboss.seam.wiki.util.PatternDeploymentHandler;
import org.jboss.seam.wiki.core.plugin.metamodel.Plugin;
import org.jboss.seam.wiki.core.exception.InvalidWikiConfigurationException;
import org.jboss.seam.core.ResourceLoader;
import org.jboss.seam.deployment.DeploymentStrategy;
import org.jboss.seam.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds (during startup scanning) all plugin messages files, using the pattern
 * <tt>[package]/[specialPluginI18Npackagename]/messages_[pluginKey]_[locale].properties</tt>.
 * Any found file name is converted into a resource bundle name and added to an internal
 * list. Other components can then read this list by getting an instance of this deployment
 * handler, during startup.
 *
 * @see PluginI18NBinder
 *
 * @author Christian Bauer
 */
public class PluginI18NDeploymentHandler extends PatternDeploymentHandler {

    public static final String NAME = "pluginI18NDeploymentHandler";
    public static final String MESSAGES_PATTERN =
        "^([a-zA-Z0-9/]+)"+Plugin.PACKAGE_I18N_MESSAGES+"_("+Plugin.KEY_PATTERN+")_([a-zA-Z_]+)\\.properties$";

    public String getPattern() {
        return MESSAGES_PATTERN;
    }

    public void handleMatch(String s, ClassLoader classLoader, String... matchedGroups) {
        if (matchedGroups == null || matchedGroups.length != 3) {
            throw new InvalidWikiConfigurationException("Deployment of i18n properties failed");
        }

        String packageName = matchedGroups[0];
        String pluginKey = matchedGroups[1];
        String locale = matchedGroups[2]; // Don't really need it here

        if (packageName.endsWith(Plugin.PACKAGE_I18N+"/")) {
            String bundleName = packageName.replaceAll("/", ".") + "messages_" + pluginKey;
            getMessageBundleNames().add(bundleName);
        }
    }

    public String getName() {
        return NAME;
    }
    
    private List<String> messageBundleNames = new ArrayList<String>();

    public List<String> getMessageBundleNames() {
        return messageBundleNames;
    }

    public static PluginI18NDeploymentHandler instance() {
        DeploymentStrategy deployment = (DeploymentStrategy) Component.getInstance("deploymentStrategy");
        return (PluginI18NDeploymentHandler) deployment.getDeploymentHandlers().get(NAME);
    }

}
