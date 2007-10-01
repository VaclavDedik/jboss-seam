package org.jboss.seam.wiki.core.dao;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.In;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.plugin.tags.TagsAggregator;
import org.hibernate.Session;
import org.hibernate.Query;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.Serializable;

@Name("tagDAO")
@AutoCreate
public class TagDAO {

    @Logger
    static Log log;

    @In
    protected EntityManager restrictedEntityManager;

    public List<TagCount> findTagsAggregatedSorted(Node startNode, Node ignoreNode, int limit) {
        List<TagCount> tagsSortedByCount = new ArrayList<TagCount>();
        List<Node> taggedNodes = findNodes(startNode, ignoreNode, null);
        for (Node taggedNode : taggedNodes) {
            String[] splitTags = taggedNode.getTags().split(",");
            for (String splitTag : splitTags) {
                String tag = splitTag.trim();

                Integer count = 1;
                TagCount newTag = new TagCount(tag, count);
                if (tagsSortedByCount.contains(newTag)) {
                    tagsSortedByCount.get(tagsSortedByCount.indexOf(newTag)).incrementCount();
                } else {
                    tagsSortedByCount.add(newTag);
                }
            }
        }
        Collections.sort(tagsSortedByCount);
        if (limit != 0 && tagsSortedByCount.size() > limit)
            return tagsSortedByCount.subList(0, limit);
        else
            return tagsSortedByCount;
    }

    public List<Node> findNodes(Node startNode, Node ignoreNode, String tag) {

        StringBuilder queryString = new StringBuilder();

        queryString.append("select n1");

        queryString.append(" ");
        queryString.append("from ").append(startNode.getTreeSuperclassEntityName()).append(" n1, ");
        queryString.append(startNode.getTreeSuperclassEntityName()).append(" n2 ");
        queryString.append("where n1.nsThread = :thread and n2.nsThread = :thread").append(" ");
        queryString.append("and n1.nsLeft between n2.nsLeft and n2.nsRight").append(" ");
        queryString.append("and n2.nsLeft > :startLeft and n2.nsRight < :startRight").append(" ");
        queryString.append("and n2.class = :clazz").append(" ");

        if (tag != null && tag.length()>0) {
            queryString.append("and n1.tags like :tag").append(" ");
        } else {
            queryString.append("and n1.tags is not null").append(" ");
            queryString.append("and length(n1.tags)>0").append(" ");
        }

        if (ignoreNode != null && ignoreNode.getId() != null)
            queryString.append("and not n1 = :ignoreNode").append(" ");

        queryString.append("order by n1.createdOn desc");

        Query nestedSetQuery = getSession().createQuery(queryString.toString());
        nestedSetQuery.setParameter("thread", startNode.getNsThread());
        nestedSetQuery.setParameter("startLeft", startNode.getNsLeft());
        nestedSetQuery.setParameter("startRight", startNode.getNsRight());
        nestedSetQuery.setParameter("clazz", "DOCUMENT"); // TODO: Hibernate can't bind the discriminator? Not even with Hibernate.CLASS type...
        if (ignoreNode != null && ignoreNode.getId() != null)
            nestedSetQuery.setParameter("ignoreNode", ignoreNode);

        if (tag != null && tag.length()>0) {
            nestedSetQuery.setParameter("tag", "%" + tag + "%");
        }

        return nestedSetQuery.list();
    }

    private Session getSession() {
        return ((Session)((org.jboss.seam.persistence.EntityManagerProxy) restrictedEntityManager).getDelegate());
    }

    public class TagCount implements Comparable, Serializable {
        String tag;
        Integer count;

        public TagCount(String tag, Integer count) {
            this.tag = tag;
            this.count = count;
        }

        public String getTag() {
            return tag;
        }

        public Integer getCount() {
            return count;
        }

        public void incrementCount() {
            count++;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TagCount tagCount = (TagCount) o;

            if (!tag.equals(tagCount.tag)) return false;

            return true;
        }

        public int hashCode() {
            return tag.hashCode();
        }

        public int compareTo(Object o) {
            int result = ((TagCount)o).getCount().compareTo( this.getCount() );
            return result == 0
                ? this.getTag().compareTo( ((TagCount)o).getTag() )
                : result;
        }
    }

}
