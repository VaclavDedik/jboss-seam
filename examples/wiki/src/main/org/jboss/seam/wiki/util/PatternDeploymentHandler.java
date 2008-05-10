/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.util;

import org.jboss.seam.deployment.DeploymentHandler;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author Christian Bauer
 */
public abstract class PatternDeploymentHandler implements DeploymentHandler {

    protected Pattern compiledPattern;

    protected PatternDeploymentHandler() {
        compiledPattern = Pattern.compile(getPattern());
    }

    public void handle(String s, ClassLoader classLoader) {
        Matcher matcher = compiledPattern.matcher(s);
        if (matcher.matches()) {
            String[] groups = new String[matcher.groupCount()];
            for (int i = 0; i < groups.length; i++) {
                groups[i] = matcher.group(i+1);
            }
            handleMatch(s, classLoader, groups);
        }
    }

    public abstract String getPattern();

    public abstract void handleMatch(String s, ClassLoader classLoader, String... matchedGroups);

}
