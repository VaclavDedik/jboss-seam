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
    private int newLinesSinceWord = 0;
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

text: { append("<p>\n"); } (plain|formatted|preformatted|quoted|para|span|list)+ { append("\n</p>\n"); } 
    ;

plain: word|punctuation|escape|space
    ;
  
formatted: bold|underline|italic|monospace|superscript|deleted
    ;

word: w:WORD { append( w.getText() ); newLinesSinceWord=0; }
    ;
    
punctuation: p:PUNCTUATION { append( p.getText() ); newLinesSinceWord=0; }
    ;
    
escape: ESCAPE { newLinesSinceWord=0; } 
        ( q:QUOTE { append( q.getText() ); } | specialChars | htmlSpecialChars )
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
    
quoted: DOUBLEQUOTE { append("<quote><p>"); newLinesSinceWord=0; }
        (plain|formatted|preformatted|para|span|list)*
        DOUBLEQUOTE { append("</p></quote>"); newLinesSinceWord=0; }
    ;

heading: ( h1 | h2 | h3 ) newline
    ;
    
headingText: (plain|formatted)+
    ;
  
h1: PLUS { append("<h1>"); } headingText { append("</h1>"); }
    ;
 
h2: PLUS PLUS { append("<h2>"); } headingText { append("</h2>"); }
    ;
 
h3: PLUS PLUS PLUS { append("<h3>"); } headingText { append("</h3>"); }
    ;
 
list: olist | ulist
    ;
    
listItemText: (plain|bold|underline|italic|monospace|superscript|deleted)*
    ;
    
olist: para { append("<ol>\n"); } (olistItem)+ { append("</ol>\n"); }
    ;
    
olistItem: HASH { append("<li>"); newLinesSinceWord=0; } listItemText { append("</li>"); } para
    ;
    
ulist: para { append("<ul>\n"); } (ulistItem)+ { append("</ul>\n"); }
    ;
    
ulistItem: EQ { append("<li>"); newLinesSinceWord=0; } listItemText { append("</li>"); } para
    ;

space: s:SPACE { append( s.getText() ); }
    ;

newline: n:NEWLINE { append( n.getText() ); }
    ;
        
para: { if (newLinesSinceWord>0) append("</p>\n"); } 
      newline 
      { if (newLinesSinceWord>0) append("<p>\n"); newLinesSinceWord++; } 
    ;

span: LT tag:WORD { append("<" + tag.getText()); }
          (
              space att:WORD EQ 
              DOUBLEQUOTE { append(att.getText() + "=\""); } 
              attributeValue 
              DOUBLEQUOTE { append("\""); } 
          )* 
      (
          (
              GT { append(">"); } 
              { newLinesSinceWord=0; } 
              (plain|formatted|preformatted|quoted|newline|span|list)* 
              { newLinesSinceWord=0; }
              LT SLASH WORD GT { append("</" + tag.getText() + ">"); }
          )
      |   (
              SLASH GT { append("/>"); } 
          )
      )
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
    
NEWLINE: '\r' '\n' | '\r' | '\n'
    ;
    