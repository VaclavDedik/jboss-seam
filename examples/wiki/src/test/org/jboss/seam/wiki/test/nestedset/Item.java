/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.nestedset;

import org.jboss.seam.wiki.core.nestedset.AbstractNestedSetNode;

import javax.persistence.*;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "ITEM")
public class Item extends AbstractNestedSetNode<Item> {

    @Id
    @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PARENT_ITEM_ID")
    @org.hibernate.annotations.ForeignKey(name = "FK_ITEM_PARENT_ITEM_ID") // Just a name for the FK constraint
    private Item parent;

    // Cascade from the root node when you insert, otherwise you need to call persist(item) in the right order,
    // in other words: You need to call persist(A) before you call persist(B) and persist(C) if B and C are
    // children of A. The order in which B and C are inserted is undefined, this is a Set - it doesn't matter
    // because we sort in-memory after loading the children of A.
    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
    // Foreign key constraint option in the database simplifies deletion of nodes and subnodes
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Set<Item> children = new HashSet<Item>();

    @Column(name = "ITEM_NAME", unique = true)
    private String name;

    public Item() {}

    public Item(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public Item getParent() {
        return parent;
    }

    public void setParent(Item parent) {
        this.parent = parent;
    }

    public Set<Item> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // TODO: Yes, this needs to go away... http://opensource.atlassian.com/projects/hibernate/browse/HHH-1615
    public String[] getTreeSuperclassPropertiesForGrouping() {
        return new String[] { "nsThread", "nsLeft", "nsRight", "id", "parent", "name"};
    }

    public String toString() {
        return getName() + " (" + getId() + ") THREAD: " + getNsThread() + " " + getNsLeft() + "|" + getNsRight(); 
    }
}
