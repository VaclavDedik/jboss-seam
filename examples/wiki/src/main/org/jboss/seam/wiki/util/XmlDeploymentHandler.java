/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.util;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jboss.seam.deployment.DeploymentHandler;
import org.jboss.seam.util.DTDEntityResolver;

import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.net.UnknownHostException;

/**
 * Seam deployment handler, can be configured in seam-deployment.properties.
 * <p>
 * Supports scanning of files with <tt>getExtension()</tt>, returns them as dom4j
 * <tt>Element</tt> (the root element of each XML file). Override the
 * <tt>isSchemaValidating()</tt> method to force DTD/schema validation.
 * </p>
 *
 * @author Christian Bauer
 */
public abstract class XmlDeploymentHandler implements DeploymentHandler {

    Map<String, InputStream> files = new HashMap<String, InputStream>();
    Map<String, Element> elements;

    public void handle(String s, ClassLoader classLoader) {
        if (s.endsWith(getExtension())) {
            files.put(s, classLoader.getResourceAsStream(s));
        }
    }

    public abstract String getExtension();

    public boolean isSchemaValidating() {
        return false;
    }

    public Map<String, Element> getDescriptorsAsXmlElements() {
        // Lazy access to streams
        if (elements == null) {
            elements = new HashMap<String, Element>();
            for (Map.Entry<String, InputStream> fileInputStream : files.entrySet()) {
                try {
                    SAXReader saxReader = new SAXReader();
                    saxReader.setMergeAdjacentText(true);

                    if (isSchemaValidating()) {
                        saxReader.setEntityResolver(new DTDEntityResolver());
                        saxReader.setValidation(true);
                        saxReader.setFeature("http://apache.org/xml/features/validation/schema",true);
                    }

                    elements.put(fileInputStream.getKey(), saxReader.read(fileInputStream.getValue()).getRootElement());

                } catch (DocumentException dex) {
                    Throwable nested = dex.getNestedException();
                    if (nested != null) {
                        if (nested instanceof FileNotFoundException) {
                            throw new RuntimeException(
                                "Can't find schema/DTD reference for file: "
                                + fileInputStream.getKey() + "':  "
                                + nested.getMessage(), dex
                            );
                        } else if (nested instanceof UnknownHostException) {
                            throw new RuntimeException(
                                "Cannot connect to host from schema/DTD reference: "
                                + nested.getMessage()
                                + " - check that your schema/DTD reference is current", dex
                            );
                        }
                    }
                    throw new RuntimeException("Could not parse XML file: " + fileInputStream.getKey() ,dex);
                } catch (Exception ex) {
                    throw new RuntimeException("Could not parse XML file: " + fileInputStream.getKey() ,ex);
                }
            }
        }
        return elements;
    }

}
