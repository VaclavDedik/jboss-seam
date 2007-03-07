package org.jboss.seam.wiki.core.model;


public class GlobalPreferences {

    private String baseURL;
    private URLRendering defaultURLRendering;
    private String permlinkSuffix;
    private String themeName;
    private String newUserInRole;
    private String passwordRegex;
    private String activationCodeSalt;
    private boolean defaultNewRevisionForEditedDocument;

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public URLRendering getDefaultURLRendering() {
        return defaultURLRendering;
    }

    public void setDefaultURLRendering(String enumName) {
        defaultURLRendering = URLRendering.valueOf(enumName);
    }

    public String getPermlinkSuffix() {
        return permlinkSuffix;
    }

    public void setPermlinkSuffix(String permlinkSuffix) {
        this.permlinkSuffix = permlinkSuffix;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getNewUserInRole() {
        return newUserInRole;
    }

    public void setNewUserInRole(String newUserInRole) {
        this.newUserInRole = newUserInRole;
    }

    public String getPasswordRegex() {
        return passwordRegex;
    }

    public void setPasswordRegex(String passwordRegex) {
        this.passwordRegex = passwordRegex;
    }

    public String getActivationCodeSalt() {
        return activationCodeSalt;
    }

    public void setActivationCodeSalt(String activationCodeSalt) {
        this.activationCodeSalt = activationCodeSalt;
    }

    public boolean isDefaultNewRevisionForEditedDocument() {
        return defaultNewRevisionForEditedDocument;
    }

    public void setDefaultNewRevisionForEditedDocument(boolean defaultNewRevisionForEditedDocument) {
        this.defaultNewRevisionForEditedDocument = defaultNewRevisionForEditedDocument;
    }

    public enum URLRendering {
        PERMLINK, WIKILINK
    }

}
