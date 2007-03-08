package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.Length;
import org.hibernate.validator.Pattern;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
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
public abstract class Node implements Serializable {

    // Uses Hibernates ability to map the same class twice, see HistoricalNode.hbm.xml
    @Transient
    private Long historicalNodeId;
    @Column(name = "NODE_REVISION", nullable = false)
    private int revision = 0;

    @Id
    @GeneratedValue
    @Column(name = "NODE_ID")
    protected Long nodeId;

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

    @Column(name = "AREA_NR", nullable = false)
    private Long areaNumber;

    // Required EAGER loading, we cast this to 'Directory' sometimes and proxy wouldn't work
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PARENT_NODE_ID", nullable = true, insertable = false, updatable = false)
    protected Node parent;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "PARENT_NODE_ID", nullable = true)
    @org.hibernate.annotations.IndexColumn(name = "NODE_POSITION")
    // TODO: We are not really using this: @org.hibernate.annotations.Filter(name = "Node.onlyMenuItems")
    @org.hibernate.annotations.BatchSize(size = 5)
    private List<Node> children = new ArrayList<Node>();

    @Column(name = "CREATED_ON", nullable = false, updatable = false)
    private Date createdOn = new Date();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY_USER_ID", nullable = false, updatable = false)
    protected User createdBy;

    @Column(name = "LAST_MODIFIED_ON")
    protected Date lastModifiedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LAST_MODIFIED_BY_USER_ID")
    protected User lastModifiedBy;

    @Column(name = "WRITE_ACCESS_LEVEL", nullable = false)
    protected int writeAccessLevel = 1000;

    @Column(name = "READ_ACCESS_LEVEL", nullable = false)
    protected int readAccessLevel = 1000;

    public Node() {}

    public Node(String name) {
        this.name = name;
    }

    /**
     * Creates copy for history archiving, increments originals revision.
     * @param original The node to make a copy of
     */
    public Node(Node original) {
        if (original == null) return;
        this.revision = original.revision;
        this.nodeId = original.nodeId;
        this.name = original.name;
        this.wikiname = original.wikiname;
        this.lastModifiedOn = original.lastModifiedOn;
    }

    // Immutable properties

    public Long getId() { return nodeId; }

    public Integer getVersion() { return version; }
    public Date getCreatedOn() { return createdOn; }
    public int getRevision() { return revision; }
    public Long getHistoryId() { return historicalNodeId; }

    // Mutable properties

    public String getName() {
         return name;
    }
    public void setName(String name) {
        this.name = name;
        makeDirty();
    }

    public String getWikiname() {
        return wikiname;
    }

    public void setWikiname(String wikiname) {
        this.wikiname = wikiname;
        makeDirty();
    }

    public boolean isMenuItem() {
        return menuItem;
    }

    public void setMenuItem(boolean menuItem) {
        this.menuItem = menuItem;
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastModifiedOn() {
        return lastModifiedOn;
    }

    public void setLastModifiedOn(Date lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    public User getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(User lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public int getWriteAccessLevel() {
        return writeAccessLevel;
    }

    public void setWriteAccessLevel(int writeAccessLevel) {
        this.writeAccessLevel = writeAccessLevel;
    }

    public int getReadAccessLevel() {
        return readAccessLevel;
    }

    public void setReadAccessLevel(int readAccessLevel) {
        this.readAccessLevel = readAccessLevel;
    }

    // Misc methods

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

    public void incrementRevision() {
        revision++;
    }

    public boolean isHistoricalRevision() {
        return historicalNodeId!=null;
    }

    public void rollback(Node revision) {
        this.name = revision.name;
        this.wikiname = revision.wikiname;
        makeDirty();
    }
}
