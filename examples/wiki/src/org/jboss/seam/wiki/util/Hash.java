package org.jboss.seam.wiki.util;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
import org.jboss.seam.annotations.Name;

@Name("hashUtil")
public class Hash {
    String hashFunction = "MD5";
    String charset      = "UTF-8";

    public String hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashFunction);
            md.update(text.getBytes(charset));
            byte[] raw = md.digest();
            return new String(Hex.encodeHex(raw));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getHashFunction() {
        return hashFunction;
    }

    public void setHashFunction(String hashFunction) {
        this.hashFunction = hashFunction;
    }

}
