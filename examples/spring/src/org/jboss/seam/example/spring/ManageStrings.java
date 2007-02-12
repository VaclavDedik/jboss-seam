/**
 *
 */
package org.jboss.seam.example.spring;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * @author youngm
 *
 */
@Name("manageStrings")
@Scope(ScopeType.EVENT)
public class ManageStrings {
    @In("#{singletonSpringStringStore}")
    private StringStore singletonSpringStringStore;
    
/*  @In("#{requestSpringStringStore}")
    private StringStore requestSpringStringStore;

    @In("#{sessionSpringStringStore}")
    private StringStore sessionSpringStringStore;*/

    @In(create=true)
    private StringStore statelessSeamStringStore;
    
    @In(create=true)
    private StringStore methodSeamStringStore;

    @In(create=true)
    private StringStore eventSeamStringStore;

    @In(create=true)
    private StringStore pageSeamStringStore;

    @In(create=true)
    private StringStore conversationSeamStringStore;

    @In(create=true)
    private StringStore sessionSeamStringStore;
    @In(create=true)
    private StringStore applicationSeamStringStore;

    private String string;

    public String storeString() {
        for(StringStore store : getStringStores()) {
            store.addString(getString());
        }
        return null;
    }

    /**
     * @return the stringStores
     */
    public List<StringStore> getStringStores() {
        List<StringStore> stringStores = new ArrayList<StringStore>();
        stringStores.add(singletonSpringStringStore);
        stringStores.add(statelessSeamStringStore);
        stringStores.add(methodSeamStringStore);
        stringStores.add(eventSeamStringStore);
        stringStores.add(pageSeamStringStore);
        stringStores.add(conversationSeamStringStore);
        stringStores.add(sessionSeamStringStore);
        stringStores.add(applicationSeamStringStore);
        return stringStores;
    }

    /**
     * @return the string
     */
    public String getString() {
        return string;
    }

    /**
     * @param string the string to set
     */
    public void setString(String string) {
        this.string = string;
    }
}
