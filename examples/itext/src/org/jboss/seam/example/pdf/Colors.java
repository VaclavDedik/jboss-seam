package org.jboss.seam.example.pdf;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;


@Name("random")
@Scope(ScopeType.SESSION)
public class Colors {
    @Factory("strokes") 
    public String[] getStrokes() {
        return new String[] {
                "solid-thin",
                "solid-thick",
                "dot1",
                "dot2"
        };
    }
   
    @Factory("colors")
    public List getAll() {
        List<String> colors = new ArrayList<String>();
        
        colors.add("red");
        colors.add("blue");
        colors.add("green");
        colors.add("black");
        colors.add("white");
        
        return colors;        
    }
    
}
