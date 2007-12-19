/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.preferences;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.preferences.PreferenceProperty;
import org.jboss.seam.wiki.preferences.PreferenceValue;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Stores preference values in three tables.
 * <pre>
 * PREF_ID  PREF_NAME     ATTRIBUTE_NAME  *_VALUE
 * 1        fooGroup       fooAttribute    ...
 * 2        fooGroup       fooAttribute    ...
 * 3        fooGroup       fooAttribute    ...
 * 4        barGroup       barAttribute    ...
 * 5        barGroup       barAttribute    ...
 *
 * PREF_ID    USER_ID
 * 2          1
 * 5          1
 *
 * PREF_ID    NODE_ID
 * 3          1
 * </pre>
 * <p>
 * Can handle Long, Double, Date, Boolean, and String values with a Hibernate UserType.
 * Could be rewritten to use one table (one-to-one between this entity and user, node) but
 * we avoid <i>some</i> nullable columns that way.
 *
 * @author Christian Bauer
*/
@TypeDefs({
    @TypeDef(name="preference_value_usertype", typeClass = PreferenceValueUserType.class)
})
@Entity
@Table(name = "PREFERENCE")
public class WikiPreferenceValue implements PreferenceValue, Serializable, Comparable {

    @Id
    @GeneratedValue(generator = "wikiSequenceGenerator")
    @Column(name = "PREF_ID")
    private Long id;

    @Version
    @Column(name = "OBJ_VERSION", nullable = false)
    private int version;

    @Column(name = "COMPONENT_NAME", nullable = false)
    private String componentName;

    @Column(name = "PROPERTY_NAME", nullable = false)
    private String propertyName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", nullable = true, updatable = false)
    @org.hibernate.annotations.ForeignKey(name = "FK_PREFERENCE_USER_ID")
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "WIKI_DOCUMENT_ID", nullable = true, updatable = false)
    @org.hibernate.annotations.ForeignKey(name = "FK_PREFERENCE_WIKI_DOCUMENT_ID")
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private WikiDocument document;

    @org.hibernate.annotations.Type(type = "preference_value_usertype")
    @org.hibernate.annotations.Columns(
        columns = {
            @Column( name="LONG_VALUE"),
            @Column( name="DOUBLE_VALUE"),
            @Column( name="TIMESTAMP_VALUE"),
            @Column( name="BOOLEAN_VALUE"),
            @Column( name="STRING_VALUE", length = 1024)
        }
	)
    private Object value;

    public WikiPreferenceValue() {}

    public WikiPreferenceValue(PreferenceProperty forProperty) {
        this.componentName = forProperty.getPreferenceComponent().getName();
        this.propertyName = forProperty.getName();
        this.property = forProperty;
    }

    public Long getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public WikiDocument getDocument() {
        return document;
    }

    public void setDocument(WikiDocument document) {
        this.document = document;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        if (this.value == null && value != null)
            setDirty(true);
        else if (this.value != null && value == null)
            setDirty(true);
        else if (this.value != null && !(this.value.equals(value)))
            setDirty(true);
        this.value = value;
    }

    public boolean isSystemAssigned() {
        return getDocument() == null && getUser() == null;
    }

    public boolean isUserAssigned() {
        return getDocument() == null && getUser() != null;
    }

    public boolean isInstanceAssigned() {
        return getDocument() != null && getUser() == null;
    }

    // Useful for provider
    @Transient
    private boolean dirty;
    public boolean isDirty() { return dirty; }
    public void setDirty(boolean dirty) { this.dirty = dirty; }

    // Reference to meta model
    @Transient
    PreferenceProperty property;
    public void setPreferenceProperty(PreferenceProperty property) { this.property = property; }
    public PreferenceProperty getPreferenceProperty() { return property; }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WikiPreferenceValue that = (WikiPreferenceValue) o;

        if (!componentName.equals(that.componentName)) return false;
        if (!propertyName.equals(that.propertyName)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = componentName.hashCode();
        result = 31 * result + propertyName.hashCode();
        return result;
    }

    public int compareTo(Object o) {
        return getPreferenceProperty().getDescription().compareTo(
                    ((PreferenceValue)o).getPreferenceProperty().getDescription()
               );
    }

    public String toString() {
        return "WikiPreferenceValue for '" + getComponentName() + "." + getPropertyName() + "'";
    }
}
