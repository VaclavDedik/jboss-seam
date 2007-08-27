/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.Length;
import org.hibernate.validator.Pattern;
import org.jboss.seam.wiki.core.nestedset.AbstractNestedSetNode;
import org.jboss.seam.wiki.core.nestedset.NestedSetNode;
import org.jboss.seam.wiki.core.preferences.WikiPreferenceValue;
import org.jboss.seam.wiki.core.search.PaddedIntegerBridge;
import org.jboss.seam.wiki.core.search.annotations.Searchable;
import org.jboss.seam.wiki.core.search.annotations.SearchableType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

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
public abstract class Node extends AbstractNestedSetNode<Node> implements Serializable {

    // Uses Hibernates ability to map the same class twice, see HistoricalNode.hbm.xml
    @Transient
    private Long historicalNodeId;
    @Column(name = "NODE_REVISION", nullable = false)
    private int revision = 0;

    @Id
    @GeneratedValue(generator = "wikiSequenceGenerator")
    @Column(name = "NODE_ID")
    @org.hibernate.search.annotations.DocumentId(name = "nodeId")
    protected Long nodeId;

    @Version
    @Column(name = "OBJ_VERSION", nullable = false)
    protected Integer version;

    @Column(name = "NAME", length = 255, nullable = false)
    @Length(min = 3, max = 255)
    @Pattern(regex="[a-zA-Z]?.+", message="Name must start with a letter")
    @org.hibernate.search.annotations.Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    @Searchable(description = "Name")
    protected String name;

    @Column(name = "WIKINAME", length = 255, nullable = false)
    protected String wikiname;

    @Column(name = "MENU_ITEM", nullable = false)
    protected boolean menuItem;

    @Column(name = "AREA_NR", nullable = false)
    private Long areaNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    // The generated DDL doesn't have a NOT NULL (the root node of the tree has a NULL parent)
    @JoinColumn(name = "PARENT_NODE_ID", nullable = true, insertable = false, updatable = false)
    @org.hibernate.annotations.ForeignKey(name = "FK_NODE_PARENT_NODE_ID")
    protected Node parent;

    @OneToMany(fetch = FetchType.LAZY)
    // TODO: The NOT NULL constraint here is a hack so ON DELETE CASCADE works, it's not in the DDL
    // Without this setting, Hibernate would null the PARENT_NODE_ID column of children when it deletes a parent,
    // we don't want that because there is a FK constraint with cascading delete on it in the database!
    @JoinColumn(name = "PARENT_NODE_ID", nullable = false)
    @org.hibernate.annotations.IndexColumn(name = "DISPLAY_POSITION")
    private List<Node> children = new ArrayList<Node>();

    @Column(name = "DISPLAY_POSITION", nullable = false, updatable = false, insertable = false)
    // Hibernate would complain for newly persistent instances if it is null...
    private Integer displayPosition = Integer.MAX_VALUE;

    @Column(name = "CREATED_ON", nullable = false, updatable = false)
    @org.hibernate.search.annotations.Field(
        index = org.hibernate.search.annotations.Index.UN_TOKENIZED,
        store = org.hibernate.search.annotations.Store.YES
    )
    @org.hibernate.search.annotations.DateBridge(resolution = org.hibernate.search.annotations.Resolution.DAY)
    @Searchable(description = "Created", type = SearchableType.PASTDATE)
    private Date createdOn = new Date();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY_USER_ID", nullable = false)
    @org.hibernate.annotations.ForeignKey(name = "FK_NODE_CREATED_BY_USER_ID")
    protected User createdBy;

    @Column(name = "LAST_MODIFIED_ON")
    @org.hibernate.search.annotations.Field(
        index = org.hibernate.search.annotations.Index.UN_TOKENIZED,
        store = org.hibernate.search.annotations.Store.YES
    )
    @org.hibernate.search.annotations.DateBridge(resolution = org.hibernate.search.annotations.Resolution.DAY)
    @Searchable(description = "Modified", type = SearchableType.PASTDATE)
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
    @org.hibernate.search.annotations.Field(
        index = org.hibernate.search.annotations.Index.UN_TOKENIZED,
        store = org.hibernate.search.annotations.Store.YES
    )
    @org.hibernate.search.annotations.FieldBridge(impl = PaddedIntegerBridge.class)
    protected int readAccessLevel;

    @OneToMany(mappedBy="node")
    private Set<WikiPreferenceValue> preferences = new HashSet<WikiPreferenceValue>();

    public Node() {}

    public Node(String name) {
        this.name = name;
    }

    /**
     * Creates copy for display or history archiving.
     * <p>
     * Does <b>NOT</b> copy the node id and object version, so the copy might as well be
     * considered transient and can be persisted right away. If you want to store the
     * copy in the audit log, call setId() manually before on the copy, passing in the
     * identifier value of the original.
     * </p>
     * @param original The node to make a copy of
     */
    public Node(Node original) {
        super(original);
        // For history/audit logging
        this.revision = original.revision;
        this.name = original.name;
        this.wikiname = original.wikiname;
        this.lastModifiedOn = original.lastModifiedOn;
        this.lastModifiedByUsername = original.lastModifiedBy != null ? original.lastModifiedBy.getUsername() : null;

        // For display
        this.displayPosition = original.getDisplayPosition();
        this.areaNumber = original.getAreaNumber();
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

    public Long getAreaNumber() {
        return areaNumber;
    }

    public void setAreaNumber(Long areaNumber) {
        this.areaNumber = areaNumber;
    }

    public boolean isMenuItem() {
        return menuItem;
    }

    public void setMenuItem(boolean menuItem) {
        this.menuItem = menuItem;
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

    public boolean vetoNestedSetUpdate() {
        return historicalNodeId != null; // Historical nodes do not cause updates of the nested set tree!
    }

    public String getTreeSuperclassEntityName() {
        return "Node";
    }

    public Class getTreeSuperclass() {
        return Node.class;
    }

    // TODO: http://opensource.atlassian.com/projects/hibernate/browse/HHH-1615
    public String[] getTreeSuperclassPropertiesForGrouping() {
        return new String[]{
            "id", "class", "version",
            "nsLeft", "nsRight", "nsThread", "parent",
            "areaNumber", "createdBy", "createdOn", "lastModifiedBy", "lastModifiedOn", "menuItem", "name", "displayPosition", 
            "readAccessLevel", "revision", "wikiname", "writeAccessLevel",
            "contentType", "filename", "filesize", "imageMetaInfo.sizeX", "imageMetaInfo.sizeY", "imageMetaInfo.thumbnail", "imageMetaInfo.thumbnailData",
            "defaultDocument", "description", "enableComments", "enableCommentForm", "nameAsTitle", "pluginsUsed",
        };
    }

    public Directory getParent() {
        return (Directory)parent; // No proxies here!
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Integer getDisplayPosition() {
        return displayPosition;
    }

    public void setDisplayPosition(Integer displayPosition) {
        this.displayPosition = displayPosition;
    }

    // Misc methods

    public String toString() {
        return getName();
        // Debug return getName() + " " + super.toString();
    }

    public Node getArea() {
        Node currentNode = this;
        // TODO: This is hardcoding the "parentless parent" logic for the wiki root
        while (currentNode.getParent() != null && currentNode.getParent().getParent() != null) {
            currentNode = currentNode.getParent();
        }
        return currentNode;
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
