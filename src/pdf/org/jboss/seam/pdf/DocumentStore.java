package org.jboss.seam.pdf;

import java.io.Serializable;
import java.util.*;

import org.jboss.seam.*;
import org.jboss.seam.annotations.*;

@Name("documentStore")
@Scope(ScopeType.SESSION)
public class DocumentStore 
    implements Serializable
{    
    private static final long serialVersionUID = -357154201942127711L;

    Map<String,DocumentData> dataStore = new HashMap<String,DocumentData>();   

    long nextId = 1;
    
    public String newId() {
        return String.valueOf(nextId++);
    }

    public void saveData(String id, String type, byte[] data) {
        dataStore.put(id, new DocumentData(type,data));
    }

    public byte[] dataForId(String id) {
        return dataStore.get(id).getData();
    }
    
    public String typeForId(String id) {
        return dataStore.get(id).getMimeType();
    }

    public static DocumentStore instance()
    {
        return (DocumentStore) Component.getInstance(DocumentStore.class, true);
    }
    
    static class DocumentData {
        byte[] data;
        String mimeType;
        
        public DocumentData(String mimeType,byte[] data) {
            super();
            this.data = data;
            this.mimeType = mimeType;
        }
        public byte[] getData() {
            return data;
        }
        public String getMimeType() {
            return mimeType;
        }
        
        
    }
}
