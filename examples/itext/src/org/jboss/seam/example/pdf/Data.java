package org.jboss.seam.example.pdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {
    String id = this.toString();

    Map<String, Number> values = new HashMap<String, Number>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getKeys() {
        return new ArrayList<String>(values.keySet());        
    }
    
    public Map<String,Number> getValues() {
        return values;
    }

    public void addValue(String key, Number value) {
        values.put(key, value);
    }
}
