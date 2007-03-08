package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ROLE")
public class Role implements Serializable, Comparable {

    @Id
    @GeneratedValue
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

    @Column(name = "ACCESS_LEVEL", nullable = false, unique = true)
    @org.hibernate.annotations.Check(
        constraints = "ACCESS_LEVEL <= 1000"
    )
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
                "Name: '" + getName() + "'";
    }

    public int compareTo(Object o) {
        return getDisplayName().compareTo(((Role)o).getDisplayName());
    }
}
