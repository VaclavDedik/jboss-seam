package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.Length;
import org.jboss.seam.wiki.core.search.annotations.Searchable;
import org.jboss.seam.wiki.core.engine.WikiMacro;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;
import java.util.LinkedHashSet;

@Entity
@Table(name = "WIKI_DOCUMENT")
@org.hibernate.annotations.ForeignKey(name = "FK_WIKI_DOCUMENT_NODE_ID")
//TODO: @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)

@org.hibernate.search.annotations.Indexed
@Searchable(description = "Documents")

public class WikiDocument extends WikiFile<WikiDocument> implements Serializable {

    @Column(name = "NAME_AS_TITLE", nullable = false)
    private boolean nameAsTitle = true;

    @Column(name = "ENABLE_COMMENTS", nullable = false)
    private boolean enableComments = false;

    @Column(name = "ENABLE_COMMENT_FORM", nullable = false)
    private boolean enableCommentForm = true;

    @Column(name = "ENABLE_COMMENTS_ON_FEEDS", nullable = false)
    private boolean enableCommentsOnFeeds = true;

    @Column(name = "HEADER", nullable = true)
    @Length(min = 0, max = 4000)
    private String header;
    @Column(name = "HEADER_MACROS", nullable = true, length = 4000)
    private String headerMacrosString;
    @Transient
    private Set<WikiMacro> headerMacros = new LinkedHashSet<WikiMacro>();

    @Column(name = "CONTENT", nullable = false)
    @Length(min = 0, max = 32768)
    @Basic(fetch = FetchType.LAZY) // Lazy loaded through bytecode instrumentation
    @org.hibernate.search.annotations.Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    @Searchable(description = "Content")
    private String content;
    @Column(name = "CONTENT_MACROS", nullable = true, length = 4000)
    private String contentMacrosString;
    @Transient
    private Set<WikiMacro> contentMacros = new LinkedHashSet<WikiMacro>();

    @Column(name = "FOOTER", nullable = true)
    @Length(min = 0, max = 4000)
    private String footer;
    @Column(name = "FOOTER_MACROS", nullable = true, length = 4000)
    private String footerMacrosString;
    @Transient
    private Set<WikiMacro> footerMacros = new LinkedHashSet<WikiMacro>();

    public WikiDocument() {
        super();
        WikiDocumentDefaults defaults = new WikiDocumentDefaults();
        setDefaults(defaults);
    }

    public WikiDocument(WikiDocumentDefaults defaults) {
        super(defaults.getDefaultName());
        setDefaults(defaults);
    }

    @Override
    @org.hibernate.search.annotations.Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    @Searchable(description = "Name")
    public String getName() {
        return super.getName();
    }

    public String getHeader() { return header; }
    public void setHeader(String header) { this.header = header; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getFooter() { return footer; }
    public void setFooter(String footer) { this.footer = footer; }

    public boolean isNameAsTitle() { return nameAsTitle; }
    public void setNameAsTitle(boolean nameAsTitle) { this.nameAsTitle = nameAsTitle; }

    public boolean isEnableComments() { return enableComments; }
    public void setEnableComments(boolean enableComments) { this.enableComments = enableComments; }

    public boolean isEnableCommentForm() { return enableCommentForm; }
    public void setEnableCommentForm(boolean enableCommentForm) { this.enableCommentForm = enableCommentForm; }

    public boolean isEnableCommentsOnFeeds() { return enableCommentsOnFeeds; }
    public void setEnableCommentsOnFeeds(boolean enableCommentsOnFeeds) { this.enableCommentsOnFeeds = enableCommentsOnFeeds; }

    public String getHeaderMacrosString() { return headerMacrosString; }
    public void setHeaderMacrosString(String headerMacrosString) { this.headerMacrosString = headerMacrosString; }

    public String getContentMacrosString() { return contentMacrosString; }
    public void setContentMacrosString(String contentMacrosString) { this.contentMacrosString = contentMacrosString; }

    public String getFooterMacrosString() { return footerMacrosString; }
    public void setFooterMacrosString(String footerMacrosString) { this.footerMacrosString = footerMacrosString; }

    public Set<WikiMacro> getHeaderMacros() { return headerMacros; }
    public void setHeaderMacros(Set<WikiMacro> headerMacros) { this.headerMacros = headerMacros; }

    public Set<WikiMacro> getContentMacros() { return contentMacros; }
    public void setContentMacros(Set<WikiMacro> contentMacros) { this.contentMacros = contentMacros; }

    public Set<WikiMacro> getFooterMacros() { return footerMacros; }
    public void setFooterMacros(Set<WikiMacro> footerMacros) { this.footerMacros = footerMacros; }

    public void flatCopy(WikiDocument original, boolean copyLazyProperties) {
        super.flatCopy(original, copyLazyProperties);
        this.nameAsTitle = original.nameAsTitle;
        this.enableComments = original.enableComments;
        this.enableCommentForm = original.enableCommentForm;
        this.enableCommentsOnFeeds = original.enableCommentsOnFeeds;
        this.headerMacrosString = original.headerMacrosString;
        this.contentMacrosString = original.contentMacrosString;
        this.footerMacrosString = original.footerMacrosString;
        this.header = original.header;
        this.footer = original.footer;
        if (copyLazyProperties) {
            this.content = original.content;
        }
    }

    public WikiDocument duplicate(boolean copyLazyProperties) {
        WikiDocument dupe = new WikiDocument();
        dupe.flatCopy(this, copyLazyProperties);
        return dupe;
    }

    public void rollback(WikiDocument revision) {
        super.rollback(revision);
        this.content = revision.content;
    }

    public boolean macroPresent(String macroName) {
        for (WikiMacro headerMacro : headerMacros) {
            if (headerMacro.getName().equals(macroName)) return true;
        }
        for (WikiMacro contentMacro : contentMacros) {
            if (contentMacro.getName().equals(macroName)) return true;
        }
        for (WikiMacro footerMacro : footerMacros) {
            if (footerMacro.getName().equals(macroName)) return true;
        }
        return false;
    }

    public String getFeedDescription() {
        return getContent();
    }

    public String getHistoricalEntityName() {
        return "HistoricalWikiDocument";
    }

    public String getPermURL(String suffix) {
        return "/" + getId() + suffix;
    }

    public String getWikiURL() {
        return "/" + getArea().getWikiname() + "/" + getWikiname();
    }

    public void setDefaults(WikiDocumentDefaults defaults) {
        setName(defaults.getDefaultName());

        setHeaderMacrosString( appendMacrosAsString(defaults.getDefaultHeaderMacros()) );
        setHeader( appendMacrosAsWikiTextString(defaults.getDefaultHeaderMacros()) + defaults.getDefaultHeader() );

        setContentMacrosString( appendMacrosAsString(defaults.getDefaultContentMacros()) );
        setContent( appendMacrosAsWikiTextString(defaults.getDefaultContentMacros()) + defaults.getDefaultContent());

        setFooterMacrosString( appendMacrosAsString(defaults.getDefaultFooterMacros()) );
        setFooter( appendMacrosAsWikiTextString(defaults.getDefaultFooterMacros()) + defaults.getDefaultFooter());

        defaults.setDefaults(this);
    }

    public void addHeaderMacro(String... macro) {
        setHeaderMacrosString( getHeaderMacrosString() + appendMacrosAsString(macro));
        setHeader( getHeader() + appendMacrosAsWikiTextString(macro));
    }

    public void addFooterMacro(String... macro) {
        setFooterMacrosString( getFooterMacrosString() + appendMacrosAsString(macro));
        setFooter( getFooter()+ appendMacrosAsWikiTextString(macro));
    }

    // TODO: The replacement methods should tokenize the strings, not replaceAll()
    public void replaceHeaderMacro(String macro, String replacement) {
        setHeaderMacrosString(getHeaderMacrosString().replaceAll(macro, replacement));
        setHeader(getHeader().replaceAll(macro, replacement));
    }

    public void replaceFooterMacro(String macro, String replacement) {
        setFooterMacrosString(getFooterMacrosString().replaceAll(macro, replacement));
        setFooter(getFooter().replaceAll(macro, replacement));
    }

    private String appendMacrosAsString(String[] macros) {
        if (macros.length == 0) return "";
        StringBuilder macrosString = new StringBuilder();
        for (String s : macros) {
            macrosString.append(s).append(" ");
        }
        return macrosString.substring(0, macrosString.length() - 1);
    }

    private String appendMacrosAsWikiTextString(String[] macros) {
        if (macros.length == 0) return "";
        StringBuilder macrosString = new StringBuilder();
        for (String s : macros) {
            macrosString.append("[<=").append(s).append("]\n");
        }
        return macrosString.toString();
    }


    public String toString() {
        return "Document (" + getId() + "): " + getName();
    }
}
