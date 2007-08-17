/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.nestedset;

import org.hibernate.Query;
import org.hibernate.StatelessSession;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.wiki.core.nestedset.NestedSetNodeWrapper;
import org.jboss.seam.wiki.core.nestedset.NestedSetResultTransformer;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;
import org.dbunit.operation.DatabaseOperation;

import javax.persistence.EntityManager;
import java.util.Comparator;

public class NestedSetTests extends DBUnitSeamTest {

    private Log log = Logging.getLog(NestedSetTests.class);

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/wiki/test/nestedset/ItemData.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void createTree() throws Exception {

        new FacesRequest("/") {

            protected void invokeApplication() throws Exception {

                EntityManager em = (EntityManager)getValue("#{entityManager}");

                /* Tree:
                      A
                     / \
                    B   C
                       / \
                      D   E
                     /\
                    F  G
                 */
                Item a = new Item("A");
                Item b = new Item("B");
                Item c = new Item("C");
                Item d = new Item("D");
                Item e = new Item("E");
                Item f = new Item("F");
                Item g = new Item("G");

                a.addChild(b);
                a.addChild(c);

                c.addChild(d);
                c.addChild(e);

                c.addChild(d);

                d.addChild(f);
                d.addChild(g);

                // Cascading persist for children collection!
                em.persist(a);

                getUserTransaction().commit();
                em.clear();

                assert checkNestedSetNodeInMemory(a, a.getId(), 1, 14);
                assert checkNestedSetChildNodeInMemory(a, b);
                assert checkNestedSetChildNodeInMemory(a, c);
                assert checkNestedSetChildNodeInMemory(c, d);
                assert checkNestedSetChildNodeInMemory(c, e);
                assert checkNestedSetChildNodeInMemory(d, f);
                assert checkNestedSetChildNodeInMemory(d, g);

                assert checkNestedSetNodeInDatabase("A", a.getId(), 1, 14);
                assert checkNestedSetChildNodeInDatabase("A", "B");
                assert checkNestedSetChildNodeInDatabase("A", "C");
                assert checkNestedSetChildNodeInDatabase("C", "D");
                assert checkNestedSetChildNodeInDatabase("C", "E");
                assert checkNestedSetChildNodeInDatabase("D", "F");
                assert checkNestedSetChildNodeInDatabase("D", "G");

                displayNodeTree(getItemFromDatabase("A"));
            }
        }.run();
    }

    @Test
    public void insertNode() throws Exception {

        new FacesRequest("/") {

            protected void invokeApplication() throws Exception {

                EntityManager em = (EntityManager)getValue("#{entityManager}");

                /* Tree:
                      A
                     / \
                    B   C
                       / \
                      D   E
                     /\
                    F  G
                 */
                Item a = new Item("A");
                Item b = new Item("B");
                Item c = new Item("C");
                Item d = new Item("D");
                Item e = new Item("E");
                Item f = new Item("F");
                Item g = new Item("G");

                a.addChild(b);
                a.addChild(c);

                c.addChild(d);
                c.addChild(e);

                c.addChild(d);

                d.addChild(f);
                d.addChild(g);

                // Cascading persist for children collection!
                em.persist(a);

                getUserTransaction().commit();
                getUserTransaction().begin();
                em.joinTransaction();

                /* Tree:
                      A
                     / \
                    B   C
                       / \
                      D   E
                     /\   |
                    F  G  H
                 */

                Item h = new Item("H");
                e.addChild(h);
                em.persist(h);

                getUserTransaction().commit();
                em.clear();

                assert checkNestedSetNodeInMemory(a, a.getId(), 1, 16);
                assert checkNestedSetChildNodeInMemory(a, b);
                assert checkNestedSetChildNodeInMemory(a, c);
                assert checkNestedSetChildNodeInMemory(c, d);
                assert checkNestedSetChildNodeInMemory(c, e);
                assert checkNestedSetChildNodeInMemory(d, f);
                assert checkNestedSetChildNodeInMemory(d, g);
                assert checkNestedSetChildNodeInMemory(e, h);

                assert checkNestedSetNodeInDatabase("A", a.getId(), 1, 16);
                assert checkNestedSetChildNodeInDatabase("A", "B");
                assert checkNestedSetChildNodeInDatabase("A", "C");
                assert checkNestedSetChildNodeInDatabase("C", "D");
                assert checkNestedSetChildNodeInDatabase("C", "E");
                assert checkNestedSetChildNodeInDatabase("D", "F");
                assert checkNestedSetChildNodeInDatabase("D", "G");
                assert checkNestedSetChildNodeInDatabase("E", "H");

                displayNodeTree(getItemFromDatabase("A"));
            }
        }.run();
    }

    @Test
    public void deleteNode() throws Exception {

        new FacesRequest("/") {

            protected void invokeApplication() throws Exception {

                EntityManager em = (EntityManager)getValue("#{entityManager}");

                /* Tree:
                      A
                     / \
                    B   C
                       / \
                      D   E
                     /\
                    F  G
                 */
                Item a = new Item("A");
                Item b = new Item("B");
                Item c = new Item("C");
                Item d = new Item("D");
                Item e = new Item("E");
                Item f = new Item("F");
                Item g = new Item("G");

                a.addChild(b);
                a.addChild(c);

                c.addChild(d);
                c.addChild(e);

                c.addChild(d);

                d.addChild(f);
                d.addChild(g);

                // Cascading persist for children collection!
                em.persist(a);

                getUserTransaction().commit();
                getUserTransaction().begin();
                em.joinTransaction();

                /* Tree:
                      A
                     / \
                    B   C
                        |
                        E
                 */

                c.removeChild(d);
                em.remove(d);

                getUserTransaction().commit();
                em.clear();

                assert checkNestedSetNodeInMemory(a, a.getId(), 1, 8);
                assert checkNestedSetChildNodeInMemory(a, b);
                assert checkNestedSetChildNodeInMemory(a, c);
                assert checkNestedSetChildNodeInMemory(c, e);

                assert checkNestedSetNodeInDatabase("A", a.getId(), 1, 8);
                assert checkNestedSetChildNodeInDatabase("A", "B");
                assert checkNestedSetChildNodeInDatabase("A", "C");
                assert checkNestedSetChildNodeInDatabase("C", "E");

                displayNodeTree(getItemFromDatabase("A"));
            }
        }.run();
    }

    @Test
    public void deleteInsertNode() throws Exception {

        new FacesRequest("/") {

            protected void invokeApplication() throws Exception {

                EntityManager em = (EntityManager)getValue("#{entityManager}");

                /* Tree:
                      A
                     / \
                    B   C
                       / \
                      D   E
                     /\
                    F  G
                 */
                Item a = new Item("A");
                Item b = new Item("B");
                Item c = new Item("C");
                Item d = new Item("D");
                Item e = new Item("E");
                Item f = new Item("F");
                Item g = new Item("G");

                a.addChild(b);
                a.addChild(c);

                c.addChild(d);
                c.addChild(e);

                c.addChild(d);

                d.addChild(f);
                d.addChild(g);

                // Cascading persist for children collection!
                em.persist(a);

                getUserTransaction().commit();
                getUserTransaction().begin();
                em.joinTransaction();

                /* Tree:
                      A
                     / \
                    B   C
                       / \
                      E   H
                 */

                c.removeChild(d);
                em.remove(d);

                Item h = new Item("H");
                c.addChild(h);
                em.persist(h);

                getUserTransaction().commit();
                em.clear();

                assert checkNestedSetNodeInMemory(a, a.getId(), 1, 10);
                assert checkNestedSetChildNodeInMemory(a, b);
                assert checkNestedSetChildNodeInMemory(a, c);
                assert checkNestedSetChildNodeInMemory(c, e);
                assert checkNestedSetChildNodeInMemory(c, h);

                assert checkNestedSetNodeInDatabase("A", a.getId(), 1, 10);
                assert checkNestedSetChildNodeInDatabase("A", "B");
                assert checkNestedSetChildNodeInDatabase("A", "C");
                assert checkNestedSetChildNodeInDatabase("C", "E");
                assert checkNestedSetChildNodeInDatabase("C", "H");

                displayNodeTree(getItemFromDatabase("A"));
            }
        }.run();
    }

    /* ############################################################################################################## */

    private boolean checkNestedSetNodeInMemory(Item node, long thread, long left, long right) throws Exception {
        return node.getNsThread() == thread && node.getNsLeft() == left && node.getNsRight() == right;
    }

    private boolean checkNestedSetChildNodeInMemory(Item parent, Item node) throws Exception {
        return node.getNsThread().equals(parent.getNsThread()) && node.getNsLeft() > parent.getNsLeft() && node.getNsRight() < parent.getNsRight();
    }

    private boolean checkNestedSetNodeInDatabase(String itemName, long thread, long left, long right) throws Exception {
        return checkNestedSetNodeInMemory( getItemFromDatabase(itemName), thread, left, right);
    }

    private boolean checkNestedSetChildNodeInDatabase(String parentName, String itemName) throws Exception {
        return checkNestedSetChildNodeInMemory( getItemFromDatabase(parentName), getItemFromDatabase(itemName));
    }

    private StatelessSession getStatelessSession() throws Exception {
        org.jboss.ejb3.entity.InjectedEntityManagerFactory jbossEMF =
                (org.jboss.ejb3.entity.InjectedEntityManagerFactory) getInitialContext().lookup("java:/entityManagerFactories/wiki");
        return ((HibernateEntityManagerFactory) jbossEMF.getDelegate()).getSessionFactory().openStatelessSession();
    }

    private Item getItemFromDatabase(String itemName) throws Exception {
        StatelessSession ss = getStatelessSession();
        // TODO: EAGER loading of parent doesn't work with StatelessSession, only if I do it in the query...
        Item item = (Item) ss.createQuery("select i from Item i left join fetch i.parent where i.name = :name").setParameter("name", itemName).uniqueResult();
        ss.close();
        return item;
    }

    private void displayNodeTree(Item startNode) throws Exception {
        if (log.isTraceEnabled()) {

            StatelessSession session = getStatelessSession();

            StringBuffer queryString = new StringBuffer();

            queryString.append("select").append(" ");
            queryString.append("count(n1.id) as nestedSetNodeLevel").append(", ");
            queryString.append("n1 as nestedSetNode").append(" ");
            queryString.append("from ").append(startNode.getTreeSuperclassEntityName()).append(" n1, ");
            queryString.append(startNode.getTreeSuperclassEntityName()).append(" n2 ");

            // TODO: EAGER loading of parent doesn't work with StatelessSession, only if I do it in the query...
            queryString.append("left join fetch n1.parent").append(" ");

            queryString.append("where n1.nsThread = :thread and n2.nsThread = :thread").append(" ");
            queryString.append("and n1.nsLeft between n2.nsLeft and n2.nsRight").append(" ");
            queryString.append("and n2.nsLeft > :startLeft and n2.nsRight < :startRight").append(" ");
            queryString.append("group by").append(" ");
            for (int i = 0; i < startNode.getTreeSuperclassPropertiesForGrouping().length; i++) {
                queryString.append("n1.").append(startNode.getTreeSuperclassPropertiesForGrouping()[i]);
                if (i != startNode.getTreeSuperclassPropertiesForGrouping().length-1) queryString.append(", ");
            }
            queryString.append(" ");
            queryString.append("order by n1.nsLeft");

            Query nestedSetQuery =  session.createQuery(queryString.toString());
            nestedSetQuery.setParameter("thread", startNode.getNsThread());
            nestedSetQuery.setParameter("startLeft", startNode.getNsLeft());
            nestedSetQuery.setParameter("startRight", startNode.getNsRight());

            // This comparator sorts the Items by name!
            Comparator<NestedSetNodeWrapper<Item>> comp =
                new Comparator<NestedSetNodeWrapper<Item>>() {
                    public int compare(NestedSetNodeWrapper<Item> o, NestedSetNodeWrapper<Item> o2) {
                        return o.getWrappedNode().getName().compareTo(o2.getWrappedNode().getName());
                    }
                };

            NestedSetNodeWrapper<Item> startNodeWrapper = new NestedSetNodeWrapper<Item>(startNode, comp, 0l);
            nestedSetQuery.setResultTransformer( new NestedSetResultTransformer<Item>(startNodeWrapper) );

            nestedSetQuery.list(); // Append all children hierarchically to the startNodeWrapper

            log.trace("######################################## TREE BEGIN #####################################################");
            displayNodes(startNodeWrapper);
            log.trace("######################################## TREE END   #####################################################");

            session.close();
        }
    }
    private void displayNodes(NestedSetNodeWrapper<Item> startNode) {
        StringBuffer levelMarkers = new StringBuffer();
        for (int i = 1; i <= startNode.getLevel(); i++) {
            levelMarkers.append("#");
        }
        log.trace(levelMarkers.toString() + " " + startNode);
        for (NestedSetNodeWrapper<Item> next : startNode.getWrappedChildren()) {
            displayNodes(next);
        }
    }


}
