/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.plugin.binding.lacewiki;

import org.dom4j.Element;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.exception.InvalidWikiConfigurationException;
import org.jboss.seam.wiki.core.plugin.PluginRegistry;
import org.jboss.seam.wiki.core.plugin.PluginCacheManager;
import org.jboss.seam.wiki.core.plugin.metamodel.MacroPluginModule;
import org.jboss.seam.wiki.core.plugin.metamodel.*;
import org.jboss.seam.wiki.core.plugin.metamodel.PluginModule;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceRegistry;

import java.util.*;

/**
 * Parses and binds plugin.xml metadata to plugin metamodel.
 *
 * @author Christian Bauer
 */
@Name("pluginBinder")
@Scope(ScopeType.APPLICATION)
@Startup
public class PluginBinder extends org.jboss.seam.wiki.core.plugin.binding.PluginBinder {

    @Logger
    Log log;

    @In
    PreferenceRegistry preferenceRegistry;

    @In
    Map<String,String> messages;

    @Create
    public void readDescriptors() {
        log.debug("reading deployment descriptors as XML...");
        descriptors = PluginDeploymentHandler.instance().getDescriptorsAsXmlElements();
    }

    Map<String, Element> descriptors = new HashMap<String, Element>();

    public void bindPlugins(PluginRegistry registry) {
        log.debug("installing plugins from XML descriptors: " + descriptors.size());

        for (Map.Entry<String, Element> descriptor : descriptors.entrySet()) {
            log.debug("installing deployment descriptor: " + descriptor.getKey());
            Element root = descriptor.getValue();

            String pluginKey = root.attributeValue("key");

            log.debug("binding plugin: " + descriptor.getKey());
            Plugin plugin = new Plugin(descriptor.getKey(), pluginKey);
            log.debug("plugin descriptor package path: " + plugin.getDescriptorPackagePath());
            registry.addPlugin(pluginKey, plugin);

            String pluginLabel = root.attributeValue("label");
            if (pluginLabel == null) pluginLabel = getMessage(plugin.getKey()+".label");
            plugin.setLabel(pluginLabel);

            bindPluginInfo(root, plugin);
            bindMacroPluginsModules(registry, root, plugin);
        }

        bindMacroParameters(registry);
    }

    private void bindPluginInfo(Element root, Plugin plugin) {
        List<Element> pluginInfos = root.elements("plugin-info");
        if (pluginInfos.size() == 1) {
            PluginInfo pluginInfo = new PluginInfo();

            pluginInfo.setVersion(pluginInfos.get(0).attributeValue("version"));

            String description = pluginInfos.get(0).attributeValue("description");
            if (description == null) description = getMessage(plugin.getKey()+".description");
            pluginInfo.setDescription(description);

            List<Element> applicationVersions = pluginInfos.get(0).elements("application-version");
            if (applicationVersions.size() == 1) {
                pluginInfo.setApplicationVersion(
                    applicationVersions.get(0).attributeValue("min"),
                    applicationVersions.get(0).attributeValue("max")
                );
            }

            List<Element> vendors = pluginInfos.get(0).elements("vendor");
            if (vendors.size() == 1) {
                pluginInfo.setVendor(
                    vendors.get(0).attributeValue("name"),
                    vendors.get(0).attributeValue("url")
                );
            }
            
            plugin.setPluginInfo(pluginInfo);
        }
    }

    private void bindMacroPluginsModules(PluginRegistry registry, Element root, Plugin plugin) {

        // Iterate through the XML descriptor and bind every <macro> to corresponding metamodel instances
        List<Element> macroPlugins = root.elements("macro");
        for (Element macroPluginModuleDescriptor : macroPlugins) {

            String moduleKey = macroPluginModuleDescriptor.attributeValue("key");
            MacroPluginModule macroPluginModule = new MacroPluginModule(plugin, moduleKey);

            log.debug("binding macro plugin module: " + macroPluginModule.getFullyQualifiedKey());


            String macroName = macroPluginModuleDescriptor.attributeValue("name");
            if (registry.getMacroPluginModulesByMacroName().containsKey(macroName)) {
                throw new InvalidWikiConfigurationException("Duplicate macro name, needs to be globally unique: " + macroName);
            }
            macroPluginModule.setName(macroName);

            String label = macroPluginModuleDescriptor.attributeValue("label");
            if (label == null) label = getMessage(plugin.getKey() + "." + moduleKey + ".label");
            macroPluginModule.setLabel(label);
            String description = macroPluginModuleDescriptor.attributeValue("description");
            if (description == null) description = getMessage(plugin.getKey() + "." + moduleKey + ".description");
            macroPluginModule.setDescription(description);

            bindMacroApplicableTo(macroPluginModuleDescriptor, macroPluginModule);
            bindMacroRenderOptions(macroPluginModuleDescriptor, macroPluginModule);

            Element skins = macroPluginModuleDescriptor.element("skins");
            if (skins != null) {
                bindMacroSkins(skins, macroPluginModule);
            }

            Element cacheRegions = macroPluginModuleDescriptor.element("cache-regions");
            if (cacheRegions != null) {
                bindFragmentCacheRegions(cacheRegions, macroPluginModule);
            }

            // Finally, bind it
            plugin.getModules().add(macroPluginModule);
            registry.getMacroPluginModulesByKey().put(macroPluginModule.getFullyQualifiedKey(), macroPluginModule);
            registry.getMacroPluginModulesByMacroName().put(macroPluginModule.getName(), macroPluginModule);
        }

    }

    private void bindFragmentCacheRegions(Element moduleDescriptor, PluginModule module) {

        List<Element> cacheRegions = moduleDescriptor.elements("cache-region");
        if (cacheRegions.size() > 0) {
            for (Element cacheRegion : cacheRegions) {

                String unqualifiedCacheRegionName = cacheRegion.attributeValue("name");
                module.addFragmentCacheRegion(unqualifiedCacheRegionName);

                List<Element> invalidationEvents = cacheRegion.elements("invalidation-event");
                if (invalidationEvents != null) {
                    for (Element invalidationEvent : invalidationEvents) {
                        String eventName = invalidationEvent.attributeValue("name");
                        PluginCacheManager.registerBinding(
                                eventName,
                                module.getQualifiedCacheRegionName(unqualifiedCacheRegionName)
                        );
                    }
                }

            }
        }
    }

    private void bindMacroSkins(Element moduleDescriptor, MacroPluginModule module) {
        List<Element> skins = moduleDescriptor.elements("skin");
        if (skins.size() > 0) {
            String[] skinNames = new String[skins.size()];
            for (int i = 0; i < skins.size(); i++)
                skinNames[i] = skins.get(i).attributeValue("name");
            module.setSkins(skinNames);
        }
    }

    private void bindMacroApplicableTo(Element moduleDescriptor, MacroPluginModule module) {
        Element applicableTo = moduleDescriptor.element("applicable-to");
        if (applicableTo != null) {
            boolean header = Boolean.parseBoolean(applicableTo.attributeValue("header"));
            boolean content = Boolean.parseBoolean(applicableTo.attributeValue("content"));
            boolean footer = Boolean.parseBoolean(applicableTo.attributeValue("footer"));
            List<MacroPluginModule.DocumentArea> applicableList = new ArrayList<MacroPluginModule.DocumentArea>();
            if (header) applicableList.add(MacroPluginModule.DocumentArea.HEADER);
            if (content) applicableList.add(MacroPluginModule.DocumentArea.CONTENT);
            if (footer) applicableList.add(MacroPluginModule.DocumentArea.FOOTER);
            MacroPluginModule.DocumentArea[] applicableArray = new MacroPluginModule.DocumentArea[applicableList.size()];
            module.setApplicableTo(applicableList.toArray(applicableArray));
        }
    }

    private void bindMacroRenderOptions(Element moduleDescriptor, MacroPluginModule module) {
        Element renderOptions = moduleDescriptor.element("render-options");
        if (renderOptions != null) {
            List<MacroPluginModule.RenderOption> renderOptionList =
                        new ArrayList<MacroPluginModule.RenderOption>();
            List<Element> options = renderOptions.elements();
            for (Element option : options) {
                if (option.getName().equals("singleton"))
                    renderOptionList.add(MacroPluginModule.RenderOption.SINGLETON);
            }
            MacroPluginModule.RenderOption[] renderOptionArray =
                    new MacroPluginModule.RenderOption[renderOptionList.size()];
            module.setRenderOptions(renderOptionList.toArray(renderOptionArray));
        }

    }

    private void bindMacroParameters(PluginRegistry registry) {
        // Iterate through all @Preference entities, look for instanceType() values (which are fully qualified macro keys)
        Set<PreferenceEntity> entitiesWithInstanceProperties =
                preferenceRegistry.getPreferenceEntities(PreferenceVisibility.INSTANCE);
        for (PreferenceEntity entity : entitiesWithInstanceProperties) {
            if (entity.getMappedTo() != null && entity.getMappedTo().length() > 0) {
                // All INSTANCE properties of this entity are for one macro, ignore whatever is configured on the properties
                String macroPluginKey = entity.getMappedTo();
                MacroPluginModule macroPluginModule = registry.getMacroPluginModulesByKey().get(macroPluginKey);
                if (macroPluginModule != null) {
                    log.debug("binding all INSTANCE properties as parameters of macro '" + macroPluginModule.getName() +"': " + entity);
                    macroPluginModule.setPreferenceEntity(entity);
                } else {
                    throw new InvalidWikiConfigurationException(
                        "Configured mapping to macro module '"+macroPluginKey+"' not found for " + entity
                    );
                }
            } else {
                // Some INSTANCE properties are for one macro, others for other macros, so we need to mix and match
                for (PreferenceEntity.Property property : entity.getPropertiesInstanceVisible()) {
                    if (property.getMappedTo() == null || property.getMappedTo().length() == 0) continue;
                    
                    String macroPluginKey = property.getMappedTo();
                    MacroPluginModule macroPluginModule = registry.getMacroPluginModulesByKey().get(macroPluginKey);
                    if (macroPluginModule != null) {
                        log.debug("binding INSTANCE property as parameter of macro '" + macroPluginModule.getName() +"': " + property);
                        macroPluginModule.setPreferenceEntity(entity);
                        macroPluginModule.getParameters().add(property);
                    } else {
                        throw new InvalidWikiConfigurationException(
                            "Configured mapping to macro module  '"+macroPluginKey+"' not found for " + property + " in " + entity
                        );
                    }
                }
            }
        }
    }

    private String getMessage(String key) {
        String message = messages.get(key);
        if (message.equals(key)) {
            throw new InvalidWikiConfigurationException("Could not find message key for label/description: " + key);
        }
        return message;
    }

}
