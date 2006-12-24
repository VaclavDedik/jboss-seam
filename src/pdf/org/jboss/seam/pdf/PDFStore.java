package org.jboss.seam.pdf;

import java.util.*;

import org.jboss.seam.*;
import org.jboss.seam.annotations.*;

@Name("pdfstore")
@Scope(ScopeType.SESSION)
public class PDFStore {
    Map<String,byte[]> dataStore = new HashMap<String,byte[]>();

    long nextId = 1;
    
    public String newId() {
        return String.valueOf(nextId++);
    }

    public void saveData(String id, byte[] data) {
        dataStore.put(id,data);
    }

    public byte[] getData(String id) {
        return dataStore.get(id);
    }

    public static PDFStore instance()
    {
        return (PDFStore) Component.getInstance(PDFStore.class, true);
    }
}
