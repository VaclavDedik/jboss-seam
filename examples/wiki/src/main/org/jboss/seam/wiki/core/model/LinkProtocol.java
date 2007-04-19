package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.Length;

import javax.persistence.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Entity
@Table(name = "LINK_PROTOCOL")
public class LinkProtocol {

    @Id
    @GeneratedValue(generator = "wikiSequenceGenerator")
    @Column(name = "LINK_PROTOCOL_ID")
    private Long id;

    @Version
    @Column(name = "OBJ_VERSION")
    protected Integer version;

    @Column(name = "PREFIX")
    @Length(min = 2, max = 10)
    @org.hibernate.validator.Pattern(regex="[a-zA-Z]+", message="Prefix must only contain letters")
    private String prefix;

    @Column(name = "LINK")
    @Length(min = 3, max = 1000)
    private String link;

    public LinkProtocol() {}

    // Immutable properties

    public Long getId() { return id; }
    public Integer getVersion() { return version; }

    // Mutable properties

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRealLink(String substitute) {
        StringBuffer realLink = new StringBuffer(getLink().length());

        Pattern regex = Pattern.compile( "(" + Pattern.quote("[") + Pattern.quote("[") + "link" +Pattern.quote("]") + Pattern.quote("]") + ")" );
        Matcher matcher = regex.matcher(getLink());

        while (matcher.find()) {
            matcher.appendReplacement(realLink, substitute);
        }
        matcher.appendTail(realLink);
        return realLink.toString();
    }
}
