package org.jboss.seam.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class ITextUtils {
    /**
     *  not all itext objects accept a string value as input,
     *  so we'll copy that logic here. 
     */
    public static int alignmentValue(String alignment) {
        if (ElementTags.ALIGN_CENTER.equalsIgnoreCase(alignment)) {
            return Element.ALIGN_CENTER;
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

        return Element.ALIGN_LEFT;
    }
}
