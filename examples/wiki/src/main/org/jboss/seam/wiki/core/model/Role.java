package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ROLE")
public class Role implements Serializable, Comparable {

    @Id
    @GeneratedValue(generator = "wikiSequenceGenerator")
    @Column(name = "ROLE_ID")
    private Long id = null;

    @Version
    @Column(name = "OBJ_VERSION")
    private int version = 0;

    @Column(name = "NAME", length = 255, nullable = false, unique = true)
    private String name;

    @Column(name = "DISPLAY_NAME", length = 255, nullable = false, unique = true)
    @Length(min = 3, max = 40)
    private String displayName;

    @Column(name = "ACCESS_LEVEL", nullable = false)
    @org.hibernate.annotations.Check(
        constraints = "ACCESS_LEVEL <= 1000"
    )

    // TODO: WTF?
    /*
    Caused by: java.lang.annotation.AnnotationTypeMismatchException:
    Incorrectly typed data found for annotation element
    public abstract long org.hibernate.validator.Max.value() (Found data of type class java.lang.Integer[1000])
     */
    //@org.hibernate.validator.Max(1000)
    private int accessLevel;

    @Column(name = "CREATED_ON", nullable = false, updatable = false)
    private Date createdOn = new Date();

    public Role() {}

    public Role(String name, String displayName, int accessLevel) {
        this.name = name;
        this.displayName = displayName;
        this.accessLevel = accessLevel;
    }

    // Immutable properties

    public Long getId() { return id; }
    public Integer getVersion() { return version; }
    public Date getCreatedOn() { return createdOn; }

    // Mutable properties

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public int getAccessLevel() { return accessLevel; }
    public void setAccessLevel(int accessLevel) { this.accessLevel = accessLevel; }

    public String toString() {
        return  "Role ('" + getId() + "'), " +
                "Access Level: '" + getAccessLevel() + "' " +
                "Name: '" + getName() + "'";
    }

    public int compareTo(Object o) {
        return Integer.valueOf(getAccessLevel()).compareTo(((Role)o).getAccessLevel());
    }

    /**
     * Used for aggregation of Role objects, by access level.
     * Also used in security checks as a handle passed into working memory.
     */
    public static class AccessLevel implements Serializable {
        private Integer accessLevel;
        private String roleNames;
        public AccessLevel(Integer accessLevel) {
            this.accessLevel = accessLevel;
        }
        public AccessLevel(Integer accessLevel, String roleNames) {
            this.accessLevel = accessLevel;
            this.roleNames = roleNames;
        }
        public Integer getAccessLevel() { return accessLevel; }
        public String getRoleNames() { return roleNames; }
        public void setRoleNames(String roleNames) { this.roleNames = roleNames; }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AccessLevel that = (AccessLevel) o;
            return accessLevel.equals(that.accessLevel);
        }
        public int hashCode() {
            return accessLevel.hashCode();
        }
        public void appendRoleName(String roleName) {
            roleNames = roleNames + ", " + roleName;
        }
    }

}
