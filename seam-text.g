header
{
package org.jboss.seam.text;
}

class SeamTextParser extends Parser;
options
{
	k=4;
	defaultErrorHandler=false;
}
{   
	private java.util.Set htmlElements = new java.util.HashSet( java.util.Arrays.asList( new String[] { "a", "p", "q", "blockquote", "code", "pre", "table", "tr", "td", "th", "ul", "ol", "li", "b", "i", "u", "tt", "del", "em", "hr", "br", "div", "span", "h1", "h2", "h3", "h4", "img"} ) );
	private java.util.Set htmlAttributes = new java.util.HashSet( java.util.Arrays.asList( new String[] { "src", "href", "lang", "class", "id", "style", "width", "height", "name", "value", "type", "cellpadding", "cellspacing", "border" } ) );

	 public class Macro {
	   public String name;
	   public java.util.SortedMap<String,String> params = new java.util.TreeMap<String,String>();

	   public Macro(String name) {
	       this.name = name;
	   }
	 }

	 private Macro currentMacro;
	
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

    protected String linkTag(String description, String url) {
        return "<a href=\"" + url + "\" class=\"seamTextLink\">" + description + "</a>";
    }

    protected String macroInclude(String macroName) {
        return "";
    }

    protected String macroInclude(Macro m) {
        return macroInclude(m.name);
    }

    protected String paragraphOpenTag() {
        return "<p class=\"seamTextPara\">\n";
    }

    protected String preformattedOpenTag() {
        return "<pre class=\"seamTextPreformatted\">\n";
    }

    protected String blockquoteOpenTag() {
        return "<blockquote class=\"seamTextBlockquote\">\n";
    }

    protected String headline1(String line) {
        return "<h1 class=\"seamTextHeadline1\">" + line + "</h1>";
    }

    protected String headline2(String line) {
        return "<h2 class=\"seamTextHeadline2\">" + line + "</h2>";
    }

    protected String headline3(String line) {
        return "<h3 class=\"seamTextHeadline3\">" + line + "</h3>";
    }

    protected String headline4(String line) {
        return "<h4 class=\"seamTextHeadline4\">" + line + "</h4>";
    }

    protected String orderedListOpenTag() {
        return "<ol class=\"seamTextOrderedList\">\n";
    }

    protected String orderedListItemOpenTag() {
        return "<li class=\"seamTextOrderedListItem\">";
    }

    protected String unorderedListOpenTag() {
        return "<ul class=\"seamTextUnorderedList\">\n";
    }

    protected String unorderedListItemOpenTag() {
        return "<li class=\"seamTextUnorderedListItem\">";
    }

    protected String emphasisOpenTag() {
        return "<i class=\"seamTextEmphasis\">";
    }

    protected String emphasisCloseTag() {
        return "</i>";
    }
}

startRule: (newline)* ( (heading (newline)* )? text (heading (newline)* text)* )?
    ;

text: ( (paragraph|preformatted|blockquote|list|html) (newline)* )+
    ;
        
paragraph: { append( paragraphOpenTag() ); } (line newlineOrEof)+ { append("</p>\n"); } newlineOrEof
    ;
    
line: (plain|formatted) (plain|formatted|preformatted|quoted|html)*
    ;
    
blockquote: DOUBLEQUOTE { append( blockquoteOpenTag() ); }
            (plain|formatted|preformatted|newline|html|list)*
            DOUBLEQUOTE newlineOrEof { append("</blockquote>\n"); }
    ;
    
preformatted: BACKTICK { append( preformattedOpenTag() ); }
              (word|punctuation|specialChars|moreSpecialChars|htmlSpecialChars|space|newline)*
              BACKTICK { append("</pre>"); }
    ;
    
plain: word|punctuation|escape|space|link|macro
    ;
  
formatted: underline|emphasis|monospace|superscript|deleted
    ;

word: an:ALPHANUMERICWORD { append( an.getText() ); } | uc:UNICODEWORD { append( uc.getText() ); }
    ;

punctuation: p:PUNCTUATION { append( p.getText() ); }
           | sq:SINGLEQUOTE { append( sq.getText() ); }
           | s:SLASH { append( s.getText() ); }
    ;
    
escape: ESCAPE ( specialChars | moreSpecialChars | evenMoreSpecialChars | htmlSpecialChars | b:BACKTICK {append( b.getText() );} )
    ;
    
specialChars:
          st:STAR { append( st.getText() ); } 
        | b:BAR { append( b.getText() ); }
        | h:HAT { append( h.getText() ); }
        | p:PLUS { append( p.getText() ); }
        | eq:EQ { append( eq.getText() ); }
        | hh:HASH { append( hh.getText() ); }
        | e:ESCAPE { append( e.getText() ); }
        | t:TWIDDLE { append( t.getText() ); }
        | u:UNDERSCORE { append( u.getText() ); }
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

link: OPEN
      { beginCapture(); } 
      (word|punctuation|escape|space)*
      { String text=endCapture(); } 
      EQ GT 
      { beginCapture(); }
      attributeValue 
      { String link = endCapture(); append(linkTag(text, link)); }
      CLOSE
    ;

/*

[<=macro[param1=value "1"][param2=value '2']]

*/
macro: OPEN
      LT EQ
      mn:ALPHANUMERICWORD { currentMacro = new Macro(mn.getText()); }
      (macroParam)*
      CLOSE
      { append( macroInclude(currentMacro) ); currentMacro = null; }
    ;

macroParam:
      OPEN
      pn:ALPHANUMERICWORD
      EQ
      { beginCapture(); }
      macroParamValue
      { String pv = endCapture(); currentMacro.params.put(pn.getText(),pv); }
      CLOSE
    ;

macroParamValue:
      ( amp:AMPERSAND       { append(amp.getText()); } |
        dq:DOUBLEQUOTE      { append(dq.getText()); } |
        sq:SINGLEQUOTE      { append(sq.getText()); } |
        an:ALPHANUMERICWORD { append(an.getText()); } |
        p:PUNCTUATION       { append(p.getText()); } |
        s:SLASH             { append(s.getText()); } |
        lt:LT               { append(lt.getText()); } |
        gt:GT               { append(gt.getText()); } |
        space | specialChars )*
    ;

emphasis: STAR { append( emphasisOpenTag() ); }
      (plain|underline|monospace|superscript|deleted|newline)+
      STAR { append( emphasisCloseTag() ); }
    ;
    
underline: UNDERSCORE { append("<u>"); }
           (plain|emphasis|monospace|superscript|deleted|newline)+
           UNDERSCORE { append("</u>"); }
    ;
    

monospace: BAR { append("<tt>"); }
           (word | punctuation | space 
          | st:STAR { append( st.getText() ); }
          | h:HAT { append( h.getText() ); }
          | p:PLUS { append( p.getText() ); }
          | eq:EQ { append( eq.getText() ); }
          | hh:HASH { append( hh.getText() ); }
          | e:ESCAPE { append( e.getText() ); }
          | t:TWIDDLE { append( t.getText() ); }
          | u:UNDERSCORE { append( u.getText() ); }
          | moreSpecialChars
          | htmlSpecialChars
          | newline)+
           BAR { append("</tt>"); }
    ;
    
superscript: HAT { append("<sup>"); }
             (plain|emphasis|underline|monospace|deleted|newline)+
             HAT { append("</sup>"); }
    ;
    
deleted: TWIDDLE { append("<del>"); }
         (plain|emphasis|underline|monospace|superscript|newline)+
         TWIDDLE { append("</del>"); }
    ;
    
quoted: DOUBLEQUOTE { append("<q>"); }
        (plain|emphasis|underline|monospace|superscript|deleted|newline)+
        DOUBLEQUOTE { append("</q>"); }
    ;

heading: ( h1 | h2 | h3 | h4 ) newlineOrEof
    ;
  
h1: PLUS
      { beginCapture(); }
      line
      { String headline=endCapture(); }
      { append(headline1(headline.trim())); }
    ;
 
h2: PLUS PLUS
      { beginCapture(); }
      line
      { String headline=endCapture(); }
      { append(headline2(headline.trim())); }
    ;

h3: PLUS PLUS PLUS
      { beginCapture(); }
      line
      { String headline=endCapture(); }
      { append(headline3(headline.trim())); }
    ;
 
h4: PLUS PLUS PLUS PLUS
      { beginCapture(); }
      line
      { String headline=endCapture(); }
      { append(headline4(headline.trim())); }
    ;

list: ( olist | ulist ) newlineOrEof
    ;
    
olist: { append( orderedListOpenTag() ); } (olistLine newlineOrEof)+ { append("</ol>\n"); }
    ;
    
olistLine: HASH { append( orderedListItemOpenTag() ); } line { append("</li>"); }
    ;
    
ulist: { append( unorderedListOpenTag() ); } (ulistLine newlineOrEof)+ { append("</ul>\n"); }
    ;
    
ulistLine: EQ { append( unorderedListItemOpenTag() ); } line { append("</li>"); }
    ;

space: s:SPACE { append( s.getText() ); }
    ;
    
newline: n:NEWLINE { append( n.getText() ); }
    ;

newlineOrEof: newline | EOF
    ;

html: openTag ( space | space attribute )* ( ( beforeBody body closeTagWithBody ) | closeTagWithNoBody ) 
    ;

body: (plain|formatted|preformatted|quoted|html|list|newline)*
    ;

openTag: LT name:ALPHANUMERICWORD { validateElement(name); append("<"); append(name.getText()); }
    ;
    
beforeBody: GT { append(">"); }
    ;
    
closeTagWithBody: LT SLASH name:ALPHANUMERICWORD GT { append("</"); append(name.getText()); append(">"); }
    ;
    
closeTagWithNoBody: SLASH GT { append("/>"); }
    ;
    
attribute: att:ALPHANUMERICWORD (space)* EQ (space)*
           DOUBLEQUOTE {  validateAttribute(att); append(att.getText()); append("=\""); } 
           attributeValue 
           DOUBLEQUOTE { append("\""); } 
    ;
        
attributeValue: ( AMPERSAND { append("&amp;"); } |
                an:ALPHANUMERICWORD { append( an.getText() ); } |
                p:PUNCTUATION { append( p.getText() ); } |
                s:SLASH { append( s.getText() ); } |
                space | specialChars )*
    ;
    
class SeamTextLexer extends Lexer;
options
{
   k=2;

   // Allow any char but \uFFFF (16 bit -1)
   charVocabulary='\u0000'..'\uFFFE';
}

// Unicode sets allowed:
// '\u00a0'..'\u00ff'  Latin 1 supplement (no control characters) http://www.unicode.org/charts/PDF/U0080.pdf
// '\u0100'..'\u017f'  Latin Extended A http://www.unicode.org/charts/PDF/U0100.pdf
// '\u0180'..'\u024f'  Latin Extended B http://www.unicode.org/charts/PDF/U0180.pdf
// '\u0250'..'\ufaff'  Various other languages, punctuation etc. (excluding "presentation forms")
// '\uff00'..'\uffef'  Halfwidth and Fullwidth forms (including CJK punctuation)

ALPHANUMERICWORD: ('a'..'z'|'A'..'Z'|'0'..'9')+
    ;

UNICODEWORD: (
         '\u00a0'..'\u00ff' |
         '\u0100'..'\u017f' |
         '\u0180'..'\u024f' |
         '\u0250'..'\ufaff' |
         '\uff00'..'\uffef'
      )+
    ;

PUNCTUATION: '-' | ';' | ':' | '(' | ')' | '{' | '}' | '?' | '!' | '@' | '%' | '.' | ',' | '$'
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
    
BACKTICK: '`'
    ;
    
TWIDDLE: '~'
    ;

DOUBLEQUOTE: '"'
    ;

SINGLEQUOTE: '\''
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

AMPERSAND: '&'
    ;

SPACE: (' '|'\t')+
    ;
    
NEWLINE: "\r\n" | '\r' | '\n'
    ;

EOF : '\uFFFF'
    ;
