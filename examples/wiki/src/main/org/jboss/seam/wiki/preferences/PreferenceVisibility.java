package org.jboss.seam.wiki.preferences;

import java.io.Serializable;

/**
 * Support for multi-level preference overrides.
 * <p>
 * You can either only use <tt>SYSTEM</tt> and have one level of preferences that can
 * be changed with a system-level preferences editor, or you can override preference values
 * for a logged-in user or even for a currently active instance (e.g. the currently visible
 * document).
 * 
 * @author Christian Bauer
 */
public enum PreferenceVisibility implements Serializable {
    SYSTEM, USER, INSTANCE
}
