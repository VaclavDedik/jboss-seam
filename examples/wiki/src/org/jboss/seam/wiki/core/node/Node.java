package org.jboss.seam.wiki.core.node;

import org.hibernate.validator.Length;
import org.hibernate.validator.Pattern;
import org.jboss.seam.wiki.core.links.WikiLinkResolver;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Collections;
import java.io.Serializable;

@Entity
@Table(
    name = "NODE",
    uniqueConstraints = {
        // Siblings in a directory can't have the same name
        @UniqueConstraint(columnNames = {"PARENT_NODE_ID", "WIKINAME"}),
        // Wikiname of a document needs to be unique within an area
        @UniqueConstraint(columnNames = {"NODE_TYPE", "AREA_NR", "WIKINAME"})
    }
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name = "NODE_TYPE",
    length = 255
)
@org.hibernate.annotations.FilterDef(
    name = "Node.onlyMenuItems",
    defaultCondition = "MENU_ITEM = 'true'"
)
public abstract class Node implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "NODE_ID")
    protected Long id;

    @Version
    @Column(name = "OBJ_VERSION")
    protected Integer version;

    @Column(name = "NAME", length = 255, nullable = false)
    @Length(min = 3, max = 255)
    @Pattern(regex="[a-zA-Z]?.+", message="Name must start with a letter")
    protected String name;

    @Column(name = "WIKINAME", length = 255, nullable = false)
    protected String wikiname;

    @Column(name = "MENU_ITEM", nullable = false)
    protected boolean menuItem;

    @Column(name = "CREATED_ON", nullable = false, updatable = false)
    private Date createdOn = new Date();

    @Column(name = "LAST_MODIFIED_ON")
    private Date lastModifiedOn;

    @Column(name = "AREA_NR", nullable = false)
    private Long areaNumber;

    @ManyToOne
    @JoinColumn(name = "PARENT_NODE_ID", nullable = true, insertable = false, updatable = false)
    protected Node parent;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "PARENT_NODE_ID", nullable = true)
    @org.hibernate.annotations.IndexColumn(name = "NODE_POSITION")
    // TODO: We are not really using this: @org.hibernate.annotations.Filter(name = "Node.onlyMenuItems")
    @org.hibernate.annotations.BatchSize(size = 5)
    private List<Node> children = new ArrayList<Node>();

    public Node() {}

    public Node(String name) {
        this.name = name;
    }

    // Immutable properties

    public Long getId() { return id; }
    public String getIdAsString() {
        // JSF is stupid
        return getId().toString();
    }
    public Integer getVersion() { return version; }
    public Date getCreatedOn() { return createdOn; }

    // Mutable properties

    public String getName() {
         return name;
    }
    public void setName(String name) {
        this.name = name;
        this.wikiname = WikiLinkResolver.convertToWikiName(name);
        makeDirty();
    }

    public String getWikiname() {
        return wikiname;
    }

    public boolean isMenuItem() {
        return menuItem;
    }

    public void setMenuItem(boolean menuItem) {
        this.menuItem = menuItem;
    }

    public Date getLastModifiedOn() {
        return lastModifiedOn;
    }

    public void setLastModifiedOn(Date lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    public Long getAreaNumber() {
        return areaNumber;
    }

    public void setAreaNumber(Long areaNumber) {
        this.areaNumber = areaNumber;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        if (child.getParent() != null) child.getParent().getChildren().remove(child);
        child.setParent(this);
        this.getChildren().add(child);
    }

    public void removeChild(Node child) {
        child.setParent(null);
        this.getChildren().remove(child);
    }

    protected void makeDirty() {
        setLastModifiedOn(new Date());
    }

    public Directory getArea() {
        Node currentNode = this;
        // TODO: This is hardcoding the "parentless parent" logic for the wiki root
        while (currentNode.getParent() != null && currentNode.getParent().getParent() != null) {
            currentNode = currentNode.getParent();
        }
        return (Directory)currentNode;
    }

}
