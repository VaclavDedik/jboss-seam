package org.jboss.seam.wiki.core.model;

public class WikiDocumentDefaults {

    public String[] getDefaultHeaderMacros() {
        return new String[0];
    }
    public String[] getDefaultContentMacros() {
        return new String[0];
    }
    public String[] getDefaultFooterMacros() {
        return new String[0];
    }

    public String getDefaultHeader() {
        return "";
    }
    public String getDefaultContent() {
        return "Edit this text...";
    }
    public String getDefaultFooter() {
        return "";
    }

    public String getDefaultName() {
        return "New Document";
    }

    public void setDefaults(WikiDocument document) {}

}
