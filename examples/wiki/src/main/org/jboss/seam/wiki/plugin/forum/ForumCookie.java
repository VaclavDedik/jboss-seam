package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.annotations.*;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;
import java.net.URLEncoder;
import java.net.URLDecoder;

/**
 * Simplifes storing of cookie values in a temporary (browser-bound)
 * coookie. Use <tt>forumCookie.addCookieValue(key, value)</tt> to add a string
 * value to the cookie value map. Do not use the characters "=" and ";" in
 * either keys or values. The cookie is also bound to the current HTTP session
 * by appending the session identifier to the value. This means that the
 * maximum lifetime of cookie values is in fact the session, not the browser
 * instance.
 * <p>
 * One issue is that Tomcat apparently re-uses the session identifier if no
 * session exists but a JSESSIONID cookie was present in the request. That means
 * this code works only halfway, because we no longer can uniquely identify
 * sessions by their identifier.
 * <p>
 * TODO: This class could be made generic (customizable cookie settings) and added to Seam?
 *
 * @author Christian Bauer
 */
@Name("forumCookie")
@AutoCreate
public class ForumCookie {

    public static final String COOKIE_NAME = "lacewiki_forum_topics";
    private static final String SESSION_KEY = "lacewiki_session_id";

    @In
    FacesContext facesContext;

    private Map<String, String> cookieValues;

    public Map<String, String> getCookieValues() {
        return cookieValues;
    }

    public void addCookieValue(String key, String value) {
        cookieValues.put(key, value);
        createCookie();
    }

    @Create
    public void create() {
        readCookie();
        if (getCookieValues() == null) {
            cookieValues = new HashMap<String, String>();
            cookieValues.put(SESSION_KEY, getSessionId());
            createCookie();
        }
    }

    private void readCookie() {
        if (getCookie() != null) {
            Map<String, String> values = decode(getCookie().getValue());
            if (values.get(SESSION_KEY).equals(getSessionId())) {
                cookieValues = values;
            }
        }
    }

    private void createCookie() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx != null) {
            HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
            Cookie newCookie = new Cookie(COOKIE_NAME, encode(cookieValues));
            newCookie.setMaxAge(-1); // Delete when browser closes
            newCookie.setPath(ctx.getExternalContext().getRequestContextPath());
            response.addCookie(newCookie);
        }
    }

    private Cookie getCookie() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx != null) {
            for (Map.Entry<String, Object> entry : ctx.getExternalContext().getRequestCookieMap().entrySet()) {
            }
            return (Cookie) ctx.getExternalContext().getRequestCookieMap().get(COOKIE_NAME);
        } else {
            return null;
        }
    }

    @Observer(value = "org.jboss.seam.loggedOut", create = true)
    public void destroyCookie() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx != null) {
            HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
            Cookie newCookie = new Cookie(COOKIE_NAME, null);
            newCookie.setMaxAge(0);
            newCookie.setPath(ctx.getExternalContext().getRequestContextPath());
            response.addCookie(newCookie);
        }
    }

    private String getSessionId() {
        return ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getSession().getId();
    }

    private String encode(Map<String, String> keyValuePairs) {
        StringBuilder cookieValue = new StringBuilder();
        for (Map.Entry<String, String> entry : keyValuePairs.entrySet()) {
            cookieValue.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
        }
        String cookieValueEncoded;
        try {
            cookieValueEncoded = URLEncoder.encode(cookieValue.toString(), "UTF-8");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return cookieValueEncoded;
    }

    private Map<String, String> decode(String cookieValue) {
        Map<String, String> keyValuePairs = new HashMap<String, String>();
        String cookieValueDecoded;
        try {
            cookieValueDecoded = URLDecoder.decode(cookieValue, "UTF-8");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        String[] keyValuePair = cookieValueDecoded.split(";");
        for (String keyValueString : keyValuePair) {
            String[] keyValue = keyValueString.split("=");
            keyValuePairs.put(keyValue[0], keyValue[1]);
        }

        return keyValuePairs;
    }

}
