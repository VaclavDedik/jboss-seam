package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.Length;
import org.hibernate.validator.Pattern;
import org.jboss.seam.wiki.core.preferences.WikiPreferenceValue;

import javax.persistence.*;
import java.util.*;
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
    name = "accessLevelFilter",
    parameters = {@org.hibernate.annotations.ParamDef(name = "currentAccessLevel", type="integer")}
)
@org.hibernate.annotations.Filter(
    name = "accessLevelFilter",
    condition = "READ_ACCESS_LEVEL <= :currentAccessLevel"
)
public abstract class Node implements Serializable {

    // Uses Hibernates ability to map the same class twice, see HistoricalNode.hbm.xml
    @Transient
    private Long historicalNodeId;
    @Column(name = "NODE_REVISION", nullable = false)
    private int revision = 0;

    @Id
    @GeneratedValue(generator = "wikiSequenceGenerator")
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
    @org.hibernate.annotations.ForeignKey(name = "FK_NODE_PARENT_NODE_ID")
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    protected Node parent;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_NODE_ID", nullable = true)
    @org.hibernate.annotations.IndexColumn(name = "NODE_POSITION")
    /* Filtering fucks up the list index... big issue to work around
    @org.hibernate.annotations.Filter(
        name = "accessLevelFilter",
        condition = "READ_ACCESS_LEVEL <= :currentAccessLevel"
    )
    */
    private List<Node> children = new ArrayList<Node>();

    @Column(name = "CREATED_ON", nullable = false, updatable = false)
    private Date createdOn = new Date();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY_USER_ID", nullable = false)
    @org.hibernate.annotations.ForeignKey(name = "FK_NODE_CREATED_BY_USER_ID")
    protected User createdBy;

    @Column(name = "LAST_MODIFIED_ON")
    protected Date lastModifiedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LAST_MODIFIED_BY_USER_ID")
    @org.hibernate.annotations.ForeignKey(name = "FK_NODE_LAST_MODIFIED_BY_USER_ID")
    protected User lastModifiedBy;

    @Transient
    protected String lastModifiedByUsername;

    @Column(name = "WRITE_ACCESS_LEVEL", nullable = false)
    protected int writeAccessLevel;

    @Column(name = "READ_ACCESS_LEVEL", nullable = false)
    protected int readAccessLevel;

    @OneToMany(mappedBy="node")
    private Set<WikiPreferenceValue> preferences = new HashSet<WikiPreferenceValue>();

    public Node() {}

    public Node(String name) {
        this.name = name;
    }

    /**
     * Creates copy for history archiving, increments originals revision.
     * <p>
     * Does <b>NOT</b> copy the node id and object version, so the copy might as well be
     * considered transient and can be persisted right away. If you want to store the
     * copy in the audit log, call setId() manually before on the copy, passing in the
     * identifier value of the original.
     * </p>
     * @param original The node to make a copy of
     */
    public Node(Node original) {
        if (original == null) return;
        this.revision = original.revision;
        this.name = original.name;
        this.wikiname = original.wikiname;
        this.lastModifiedOn = original.lastModifiedOn;
        this.lastModifiedByUsername = original.lastModifiedBy != null ? original.lastModifiedBy.getUsername() : null;
    }

    // Immutable properties

    public Integer getVersion() { return version; }
    public Date getCreatedOn() { return createdOn; }
    public int getRevision() { return revision; }
    public Long getHistoryId() { return historicalNodeId; }

    // Mutable properties

    public Long getId() { return nodeId; }
    public void setId(Long nodeId) { this.nodeId = nodeId; }

    public String getName() {
         return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getWikiname() {
        return wikiname;
    }

    public void setWikiname(String wikiname) {
        this.wikiname = wikiname;
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
        this.lastModifiedByUsername = lastModifiedBy.getUsername();
    }

    public String getLastModifiedByUsername() {
        return lastModifiedByUsername;
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

    public Set<WikiPreferenceValue> getPreferences() {
        return preferences;
    }

    public void setPreferences(Set<WikiPreferenceValue> preferences) {
        this.preferences = preferences;
    }

    // Misc methods

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
    }
}
