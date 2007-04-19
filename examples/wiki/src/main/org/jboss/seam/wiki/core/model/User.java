package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;
import org.hibernate.validator.Length;
import org.hibernate.validator.Email;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "USERS")
public class User implements Serializable {

    @Id
    @GeneratedValue(generator = "wikiSequenceGenerator")
    @Column(name = "USER_ID")
    private Long id = null;

    @Version
    @Column(name = "OBJ_VERSION")
    private int version = 0;

    @Column(name = "FIRSTNAME", length = 63, nullable = false)
    @NotNull
    @Length(min = 3, max = 63)
    @Pattern(regex="[a-zA-Z]+", message="First name must only contain letters")
    private String firstname;

    @Column(name = "LASTNAME", length = 63, nullable = false)
    @NotNull
    @Length(min = 3, max = 63)
    @Pattern(regex="[a-zA-Z]+", message="Last name must only contain letters")
    private String lastname;

    @Column(name = "USERNAME", length = 16, nullable = false, unique = true)
    @NotNull
    @Length(min = 3, max = 16)
    @Pattern(regex="[a-zA-Z]?[a-zA-Z0-9]+",
          message="Member name must start with a letter, and only contain letters and numbers")
    private String username; // Unique and immutable

    @Column(name = "PASSWORDHASH", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "EMAIL", length = 255, nullable = false)
    @NotNull @Email
    private String email;

    @Column(name = "ACTIVATED", nullable = false)
    private boolean activated = false;

    @Column(name = "ACTIVATION_CODE", length = 255, nullable = true)
    private String activationCode;

    @Column(name = "CREATED_ON", nullable = false, updatable = false)
    private Date createdOn = new Date();

    @ManyToMany(fetch = FetchType.LAZY) // Lazy so our @OrderBy works
    @JoinTable(
        name = "USER_ROLE",
        joinColumns = @JoinColumn(name = "USER_ID"),
        inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    @OrderBy("accessLevel desc, displayName asc")
    @org.hibernate.annotations.ForeignKey(name = "USER_ROLE_USER_ID", inverseName = "USER_ROLE_ROLE_ID")
    private List<Role> roles = new ArrayList<Role>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "MEMBER_HOME_NODE_ID", nullable = true)
    @org.hibernate.annotations.ForeignKey(name = "FK_USER_MEMBER_HOME_NODE_ID")
    private Directory memberHome;

    public User() {}

    public User(String firstname, String lastname,
                String username, String passwordHash, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
    }

    // Immutable properties

    public Long getId() { return id; }
    public Integer getVersion() { return version; }
    public Date getCreatedOn() { return createdOn; }

    // Mutable properties

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getFullname() {
        return getFirstname() + " " + getLastname();
    }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isActivated() { return activated; }
    public void setActivated(boolean activated) { this.activated = activated; }

    public String getActivationCode() { return activationCode; }
    public void setActivationCode(String activationCode) { this.activationCode = activationCode; }

    public Directory getMemberHome() { return memberHome; }
    public void setMemberHome(Directory memberHome) { this.memberHome = memberHome; }

    public List<Role> getRoles() { return roles; }
    public void setRoles(List<Role> roles) { this.roles = roles; }

    // Misc methods

    public String toString() {
        return  "User ('" + getId() + "'), " +
                "Username: '" + getUsername() + "'";
    }
    
}


