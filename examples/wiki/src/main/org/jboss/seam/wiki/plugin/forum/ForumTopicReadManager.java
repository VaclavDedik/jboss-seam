package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.ScopeType;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

@Name("forumTopicReadManager")
@Scope(ScopeType.SESSION)
@AutoCreate
public class ForumTopicReadManager implements Serializable {

    Map<Long, Set<Long>> readTopics = new HashMap<Long, Set<Long>>();

    public Map<Long, Set<Long>> getReadTopics() {
        return readTopics;
    }

    public void addTopicId(Long forumId, Long topicId) {
        if (readTopics.get(forumId) == null) {
            readTopics.put(forumId, new HashSet<Long>());
        }
        readTopics.get(forumId).add(topicId);
    }

    public void removeTopicId(Long forumId, Long topicId) {
        if (readTopics.get(forumId) != null) {
            readTopics.get(forumId).remove(topicId);
        }
    }

    public boolean isTopicIdRead(Long forumId, Long topicId) {
        return readTopics.get(forumId) != null && readTopics.get(forumId).contains(topicId);
    }

}
