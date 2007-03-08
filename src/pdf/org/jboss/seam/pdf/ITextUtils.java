package org.jboss.seam.pdf;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

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
        return ElementTags.alignmentValue(alignment);     
    }
    
    public static Rectangle pageSizeValue(String name)  {
        return PageSize.getRectangle(name);
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
