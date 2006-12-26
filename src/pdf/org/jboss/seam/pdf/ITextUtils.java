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
}
