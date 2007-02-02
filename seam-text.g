header
{
package org.jboss.seam.text;
}

class SeamTextParser extends Parser;
options
{
	k=4;
}
{   
	private java.util.Set htmlElements = new java.util.HashSet( java.util.Arrays.asList( new String[] { "a", "p", "quote", "code", "pre", "table", "tr", "td", "th", "ul", "ol", "li", "b", "i", "u", "tt", "del", "em", "hr", "br", "div", "span", "h1", "h2", "h3", "h4", "img" } ) );
	private java.util.Set htmlAttributes = new java.util.HashSet( java.util.Arrays.asList( new String[] { "src", "href", "lang", "class", "id" } ) );
	
    private StringBuilder mainBuilder = new StringBuilder();
    private StringBuilder builder = mainBuilder;
    
    public String toString() {
        return builder.toString();
    }
    
    private void append(String... strings) {
        for (String string: strings) builder.append(string);
    }
    
    private static boolean hasMultiple(String string, char c) {
        return string.indexOf(c)!=string.lastIndexOf(c);
    }
    
    private void validateElement(Token t) throws NoViableAltException {
        if ( !htmlElements.contains( t.getText().toLowerCase() ) ) {
            throw new NoViableAltException(t, null);
        }
    }

    private void validateAttribute(Token t) throws NoViableAltException {
        if ( !htmlAttributes.contains( t.getText().toLowerCase() ) ) {
            throw new NoViableAltException(t, null);
        }
    }
    
    private void beginCapture() {
        builder = new StringBuilder();
    }
    
    private String endCapture() {
        String result = builder.toString();
        builder = mainBuilder;
        return result;
    }
    
    protected String linkUrl(String linkText) { return linkText.trim(); }

    protected String linkDescription(String descriptionText, String linkText) { 
        return descriptionText.toString().trim().length()>0 ? descriptionText : linkText; 
    }

    protected String linkClass(String linkText) { return "seamTextLink"; }
}

startRule: (newline)* ( (heading (newline)* )? text (heading (newline)* text)* )?
    ;

text: ( (paragraph|special|html) (newline)* )+
    ;
    
special: (preformatted|quoted|list) newlineOrEof
    ;

paragraph: { append("<p>\n"); } (line newlineOrEof)+ { append("</p>\n"); } newlineOrEof
    ;
    
line: (plain|formatted) (plain|formatted|preformatted|quoted|html)*
    ;
    
formatted: bold|underline|italic|monospace|superscript|deleted
    ;

plain: word|punctuation|escape|space|link|entity
    ;
  
word: w:WORD { append( w.getText() ); }
    ;

punctuation: p:PUNCTUATION { append( p.getText() ); }
    ;
    
escape: ESCAPE ( specialChars | moreSpecialChars | evenMoreSpecialChars | htmlSpecialChars )
    ;
    
specialChars:
          st:STAR { append( st.getText() ); } 
        | sl:SLASH { append( sl.getText() ); } 
        | b:BAR { append( b.getText() ); } 
        | h:HAT { append( h.getText() ); }
        | m:MINUS { append( m.getText() ); } 
        | p:PLUS { append( p.getText() ); } 
        | eq:EQ { append( eq.getText() ); }
        | hh:HASH { append( hh.getText() ); }
        | e:ESCAPE { append( e.getText() ); }
        | t:TWIDDLE { append( t.getText() ); }
        | u:UNDERSCORE { append( u.getText() ); }
        | sc:SEMICOLON { append( sc.getText() ); }
    ;
    
moreSpecialChars:
          o:OPEN { append( o.getText() ); }
        | c:CLOSE { append( c.getText() ); }
    ;
    
evenMoreSpecialChars: 
          q:QUOTE { append( q.getText() ); }
    ;

htmlSpecialChars: 
      GT { append("&gt;"); } 
    | LT { append("&lt;"); } 
    | DOUBLEQUOTE { append("&quot;"); } 
    | AMPERSAND { append("&amp;"); }
    ;
    
entity: AMPERSAND { append("&amp;"); } 
        ( HASH { append("#"); } )? 
        word 
        SEMICOLON { append(";"); }
    ;
    
link: OPEN 
      { beginCapture(); } 
      (plain)* 
      { String text=endCapture(); } 
      EQ GT 
      { beginCapture(); }
      attributeValue 
      { String link = endCapture(); append("<a href=\""); append( linkUrl(link) ); append("\" class=\""); append( linkClass(link) ); append("\">"); append( linkDescription(text, link) ); append("</a>"); }
      CLOSE
    ;
    
bold: STAR { append("<b>"); }
      (plain|underline|italic|monospace|superscript|deleted|newline)+
      STAR { append("</b>"); }
    ;
    
underline: UNDERSCORE { append("<u>"); }
           (plain|bold|italic|monospace|superscript|deleted|newline)+
           UNDERSCORE { append("</u>"); }
    ;
    
italic: SLASH { append("<i>"); }
        (plain|bold|underline|monospace|superscript|deleted|newline)+
        SLASH { append("</i>"); }
    ;
    
monospace: BAR { append("<tt>"); }
           (plain|bold|underline|italic|superscript|deleted|newline)+
           BAR { append("</tt>"); }
    ;
    
superscript: HAT { append("<sup>"); }
             (plain|bold|underline|italic|monospace|deleted|newline)+
             HAT { append("</sup>"); }
    ;
    
deleted: MINUS { append("<del>"); }
         (plain|bold|underline|italic|monospace|superscript|newline)+
         MINUS { append("</del>"); }
    ;
    
preformatted: BACKTICK { append("<pre>"); }
              (word|punctuation|specialChars|moreSpecialChars|htmlSpecialChars|space|simpleNewline)*
              BACKTICK { append("</pre>"); }
    ;
    
quoted: DOUBLEQUOTE { append("<quote>"); }
        (plain|formatted|preformatted|html|(list newline)|newline)*
        DOUBLEQUOTE { append("</quote>"); }        
    ;

heading: ( h1 | h2 | h3 | h4 ) newlineOrEof
    ;
  
h1: PLUS { append("<h1>"); } line { append("</h1>"); }
    ;
 
h2: PLUS PLUS { append("<h2>"); } line { append("</h2>"); }
    ;
 
h3: PLUS PLUS PLUS { append("<h3>"); } line { append("</h3>"); }
    ;
 
h4: PLUS PLUS PLUS PLUS { append("<h4>"); } line { append("</h4>"); }
    ;
 
list: olist | ulist
    ;
    
olist: { append("<ol>\n"); } (olistLine newlineOrEof)+ { append("</ol>\n"); }
    ;
    
olistLine: HASH { append("<li>"); } line { append("</li>"); }
    ;
    
ulist: { append("<ul>\n"); } (ulistLine newlineOrEof)+ { append("</ul>\n"); }
    ;
    
ulistLine: EQ { append("<li>"); } line { append("</li>"); }
    ;

space: s:SPACE { append( s.getText() ); }
    ;
    
simpleNewline: n:NEWLINE { append( n.getText() ); }
    ;

newline: SEMICOLON { append("\n"); } | simpleNewline
    ;
    
newlineOrEof: newline | EOF
    ;

html: openTag (attribute)* ( ( beforeBody body closeTagWithBody ) | closeTagWithNoBody ) 
    ;

body: (plain|formatted|preformatted|quoted|html|(list newline)|newline)*
    ;

openTag: LT name:WORD { validateElement(name); append("<"); append(name.getText()); }
    ;
    
beforeBody: GT { append(">"); }
    ;
    
closeTagWithBody: LT SLASH name:WORD GT { append("</"); append(name.getText()); append(">"); }
    ;
    
closeTagWithNoBody: SLASH GT { append("/>"); } 
    ;
    
attribute: space att:WORD EQ 
           DOUBLEQUOTE {  validateAttribute(att); append(att.getText()); append("=\""); } 
           attributeValue 
           DOUBLEQUOTE { append("\""); } 
    ;
        
attributeValue: ( AMPERSAND { append("&amp;"); } | word | punctuation | space | specialChars )*
    ;
    
class SeamTextLexer extends Lexer;
options
{
   k=2;

   // Allow any char but \uFFFF (16 bit -1)
   charVocabulary='\u0000'..'\uFFFE';
}

WORD: ('a'..'z'|'A'..'Z'|'0'..'9'|
      '\u00c0'..'\u00d6' |
      '\u00d8'..'\u00f6' |
      '\u00f8'..'\u00ff' |
      '\u0100'..'\u1fff' |
      '\u3040'..'\u318f' |
      '\u3300'..'\u337f' |
      '\u3400'..'\u3d2d' |
      '\u4e00'..'\u9fff' |
      '\uf900'..'\ufaff')+
    ;
    
PUNCTUATION: ':' | '(' | ')' | '?' | '!' | '@' | '%' | '.' | ',' | '\''
    ;
    
EQ: '='
    ;
    
PLUS: '+'
    ;
    
UNDERSCORE: '_'
    ;
    
STAR: '*'
    ;
    
SLASH: '/'
    ;
    
ESCAPE: '\\'
    ;
    
BAR: '|'
    ;
    
MINUS: '-'
    ;
    
BACKTICK: '`'
    ;
    
TWIDDLE: '~'
    ;

DOUBLEQUOTE: '"'
    ;
    
OPEN: '['
    ;
    
CLOSE: ']'
    ;

HASH: '#'
    ;
    
HAT: '^'
    ;
    
GT: '>'
    ;
    
LT: '<'
    ;
    
SEMICOLON: ';'
    ;
    
AMPERSAND: '&'
    ;
    
SPACE: (' '|'\t')+
    ;
    
NEWLINE: "\r\n" | '\r' | '\n'
    ;

EOF : '\uFFFF'
    ;
