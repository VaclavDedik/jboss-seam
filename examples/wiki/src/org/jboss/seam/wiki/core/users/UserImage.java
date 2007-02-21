package org.jboss.seam.wiki.core.users;

import org.jboss.seam.annotations.Name;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "USER_IMAGE")
@Name("userImage")
public class UserImage implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "USER_IMAGE_ID")
    private Long id;

    @Version
    @Column(name = "OBJ_VERSION")
    protected Integer version;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Lob
    @Column(name = "IMAGE_DATA")
    private byte[] data;

    @Column(name = "CONTENT_TYPE")
    private String contentType;

    @Column(name = "CREATED_ON", nullable = false, updatable = false)
    private Date createdOn = new Date();

    public UserImage(User user, byte[] data, String contentType) {
        this.user = user;
        this.data = data;
        this.contentType = contentType;
    }

    // Immutable properties

    public Long getId() { return id; }
    public Integer getVersion() { return version; }
    public Date getCreatedOn() { return createdOn; }


    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

}
