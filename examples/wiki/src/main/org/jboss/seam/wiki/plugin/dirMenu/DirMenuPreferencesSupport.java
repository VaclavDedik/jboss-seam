package org.jboss.seam.wiki.plugin.dirMenu;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;
import org.jboss.seam.wiki.preferences.metamodel.PreferencesSupport;

import java.util.HashSet;
import java.util.Set;

@Name("dirMenuPreferencesSupport")
public class DirMenuPreferencesSupport extends PreferencesSupport {

    public Set<PreferenceEntity> getPreferenceEntities() {
        return new HashSet<PreferenceEntity>() {{
            add( createPreferenceEntity(DirMenuPreferences.class) );
        }};
    }

/* TODO: Too complicated for now...

    @Name("dirMenuQualityPreferenceValueTemplate")
    @Scope(ScopeType.CONVERSATION)
    public static class DirMenuQualityTemplate implements PreferenceValueTemplate {

        public List<String> getTemplateValues() {
            return Collections.emptyList();
        }

        public String getConverterComponentName() {
            return "dirMenuQualityConverter";
        }
    }

    @Name("dirMenuQualityConverter")
    @org.jboss.seam.annotations.faces.Converter
    public static class DirMenuQualityConverter implements Converter, Serializable {

        public Object getAsObject(javax.faces.context.FacesContext facesContext, UIComponent uiComponent, String s) {
            return s != null ? DirMenuQuality.valueOf(s) : null;
        }

        public String getAsString(javax.faces.context.FacesContext facesContext, UIComponent uiComponent, Object o) {
            return o instanceof DirMenuQuality ? ((DirMenuQuality)o).name() : null;
        }

    }
    */
}
