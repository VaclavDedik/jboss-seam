package org.jboss.seam.example.pdf;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;


@Name("colors")
@Scope(ScopeType.SESSION)
public class Colors {

   
    @Unwrap
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
