package org.jboss.seam.pdf;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.lowagie.text.Element;
import com.lowagie.text.ElementTags;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;

public class ITextUtils {
	static Map<String,Color> colorMap = new HashMap<String,Color>(); 
	
	static {
		colorMap.put("white", Color.white);
		colorMap.put("gray", Color.gray);
		colorMap.put("lightgray", Color.lightGray);
		colorMap.put("darkgray", Color.darkGray);
		colorMap.put("black", Color.black);
		colorMap.put("red", Color.red);
		colorMap.put("pink", Color.pink);
		colorMap.put("yellow", Color.yellow);
		colorMap.put("green", Color.green);
		colorMap.put("magenta", Color.magenta);
		colorMap.put("cyan", Color.cyan);
		colorMap.put("blue", Color.blue);
	}

	
    /**
     *  not all itext objects accept a string value as input,
     *  so we'll copy that logic here. 
     */
    public static int alignmentValue(String alignment) {
        if (ElementTags.ALIGN_CENTER.equalsIgnoreCase(alignment)) {
            return Element.ALIGN_CENTER;
        }
        if (ElementTags.ALIGN_LEFT.equalsIgnoreCase(alignment)) {
            return Element.ALIGN_LEFT;
        }
        if (ElementTags.ALIGN_RIGHT.equalsIgnoreCase(alignment)) {
            return Element.ALIGN_RIGHT;
        }
        if (ElementTags.ALIGN_JUSTIFIED.equalsIgnoreCase(alignment)) {
            return Element.ALIGN_JUSTIFIED;
        }
        if (ElementTags.ALIGN_JUSTIFIED_ALL.equalsIgnoreCase(alignment)) {
            return Element.ALIGN_JUSTIFIED_ALL;
        }
        if (ElementTags.ALIGN_TOP.equalsIgnoreCase(alignment)) {
            return Element.ALIGN_TOP;
        }
        if (ElementTags.ALIGN_MIDDLE.equalsIgnoreCase(alignment)) {
            return Element.ALIGN_MIDDLE;
        }
        if (ElementTags.ALIGN_BOTTOM.equalsIgnoreCase(alignment)) {
            return Element.ALIGN_BOTTOM;
        }
        if (ElementTags.ALIGN_BASELINE.equalsIgnoreCase(alignment)) {
            return Element.ALIGN_BASELINE;
        }

        return Element.ALIGN_UNDEFINED;
    }
    
    public static Rectangle pageSizeValue(String name)  {
        if (name.indexOf(' ') != -1) {            
            int[] sizes = stringToIntArray(name);
            if (sizes.length != 2) {
                throw new RuntimeException("page size should contain two integer numbers");
            }
            return new Rectangle(sizes[0],sizes[1]);
        } 
        
        try {            
            Field field = PageSize.class.getDeclaredField(name.toUpperCase());
            return (Rectangle) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Can't find page size " + name);          
        }
    }
    
    
    /**
     * return a color value from a string specification.  
     */ 
    public static Color colorValue(String colorName) {
    	Color color = colorMap.get(colorName.toLowerCase());
    	if (color == null) {
    	  color = Color.decode(colorName);
    	}
    	return color;
    }

    public static float[] stringToFloatArray(String text) {
    	String[] parts = text.split("\\s");
    	float[]  values = new float[parts.length];
    	for (int i=0;i<parts.length;i++) {
    	   values[i] = Float.valueOf(parts[i]);
    	}
    	
    	return values;
    }
    
    
    public static int[] stringToIntArray(String text) {
        String[] parts = text.split("\\s");
        int[]  values = new int[parts.length];
        for (int i=0;i<parts.length;i++) {
           values[i] = Integer.valueOf(parts[i]);
        }
        
        return values;
    }
}
