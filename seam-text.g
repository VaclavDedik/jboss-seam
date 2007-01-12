header
{
package org.jboss.seam.text;
}

class P extends Parser;
options
{
	k=3;
}
{   
    private StringBuilder builder = new StringBuilder();
    
    public String toString() {
        return builder.toString();
    }
    
    public void append(String... strings) {
        for (String string: strings) builder.append(string);
    }
    
    private static boolean hasMultiple(String string, char c) {
        return string.indexOf(c)!=string.lastIndexOf(c);
    }
}

startRule: ( (heading)? text (heading text)* )?
    ;

text: ( (paragraph|special|html) (newline)* )+
    ;
    
special: (preformatted|quoted|list) newlineOrEof
    ;

paragraph: { append("<p>\n"); } (line newline)+ { append("</p>\n"); } newlineOrEof
    ;
    
line: (plain|formatted) (plain|formatted|inlineTag)*
    ;
    
plain: word|punctuation|escape|space
    ;
  
formatted: bold|underline|italic|monospace|superscript|deleted
    ;

word: w:WORD { append( w.getText() ); }
    ;
    
punctuation: p:PUNCTUATION { append( p.getText() ); }
    ;
    
escape: ESCAPE ( q:QUOTE { append( q.getText() ); } | specialChars | htmlSpecialChars )
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
        | u:UNDERSCORE { append( u.getText() ); }
    ;

htmlSpecialChars: 
      GT { append("&gt;"); } 
    | LT { append("&lt;"); } 
    | DOUBLEQUOTE { append("&quot;"); } 
    | AMPERSAND { append("&amp;"); }
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
    
preformatted: QUOTE { append("<pre>"); }
              (word|punctuation|specialChars|htmlSpecialChars|space|newline)*
              QUOTE { append("</pre>"); }
    ;
    
quoted: DOUBLEQUOTE { append("<quote>"); }
        (plain|formatted|preformatted|html|(list newline)|newline)*
        //line (newline line)*
        DOUBLEQUOTE { append("</quote>"); }        
    ;

heading: ( h1 | h2 | h3 ) newlineOrEof
    ;
  
h1: PLUS { append("<h1>"); } line { append("</h1>"); }
    ;
 
h2: PLUS PLUS { append("<h2>"); } line { append("</h2>"); }
    ;
 
h3: PLUS PLUS PLUS { append("<h3>"); } line { append("</h3>"); }
    ;
 
list: olist | ulist
    ;
    
olist: { append("<ol>\n"); } (olistLine newline)+ { append("</ol>\n"); }
    ;
    
olistLine: HASH { append("<li>"); } line { append("</li>"); }
    ;
    
ulist: { append("<ul>\n"); } (ulistLine newline)+ { append("</ul>\n"); }
    ;
    
ulistLine: EQ { append("<li>"); } line { append("</li>"); }
    ;

space: s:SPACE { append( s.getText() ); }
    ;

newline: n:NEWLINE { append( n.getText() ); }
    ;
    
newlineOrEof: newline | EOF
    ;

html: openTag (attribute)* ( ( tagContent htmlText closeTagWithContent ) | closeTagWithNoContent ) 
    ;

htmlText: (plain|formatted|preformatted|quoted|html|(list newline)|newline)*
    ;
    
inlineTag: openTag (attribute)* ( ( tagContent inlineTagText closeTagWithContent ) | closeTagWithNoContent )
    ;
    
inlineTagText: (plain|formatted)*
    ;

openTag: LT name:WORD { append("<"); append(name.getText()); }
    ;
    
tagContent: GT { append(">"); }
    ;
    
closeTagWithContent: LT SLASH name:WORD GT { append("</"); append(name.getText()); append(">"); }
    ;
    
closeTagWithNoContent: SLASH GT { append("/>"); } 
    ;
    
attribute: space att:WORD EQ 
           DOUBLEQUOTE { append(att.getText() + "=\""); } 
           attributeValue 
           DOUBLEQUOTE { append("\""); } 
    ;
        
attributeValue: ( AMPERSAND { append("&amp;"); } | word | punctuation | space | specialChars )*
    ;
    
class L extends Lexer;
options
{
	k=2;
}

WORD: ('a'..'z'|'A'..'Z'|'0'..'9')+
    ;
    
PUNCTUATION: ':' | ';' | '(' | ')' | '?' | '!' | '@' | '%' | '.' | ','
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
    
QUOTE: '\''
    ;

DOUBLEQUOTE: '"'
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
