package org.jboss.seam.web;

public class Pattern 
{
    String view;
    String pattern;
    
    IncomingPattern inPattern;
    OutgoingPattern outPattern;

    public Pattern(String view, String pattern) {
        this.view = view;
        this.pattern = pattern;
        
        inPattern = new IncomingPattern(view, pattern);
        outPattern = new OutgoingPattern(view, pattern);
    }

    public Rewrite matchIncoming(String path) {
        return returnIfMatch(inPattern.rewrite(path));
    }

    public Rewrite matchOutgoing(String path) {
        return returnIfMatch(outPattern.rewrite(path));
    }
    
    @Override
    public String toString() {
        return "Pattern(" + view + ":" + pattern + ")";
    }

    private Rewrite returnIfMatch(Rewrite rewrite) {
        return rewrite.isMatch() ? rewrite : null;
    }
}
