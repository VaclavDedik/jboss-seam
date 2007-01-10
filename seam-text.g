header
{
package org.jboss.seam.text;
}

class P extends Parser;
options
{
	k=2;
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

startRule: { append("<p>\n"); }
           text
           { append("\n</p>\n"); }
    ;

text: (word|punctuation|formatting|escape|space|para|span|list)*
    ;
    
formatting: bold|underline|italic|monospace|superscript|deleted|preformatted|quoted
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
      (word|punctuation|escape|underline|italic|monospace|superscript|deleted|space|newline)+
      STAR { append("</b>"); }
    ;
    
underline: UNDERSCORE { append("<u>"); }
           (word|punctuation|escape|bold|italic|monospace|superscript|deleted|space|newline)+
           UNDERSCORE { append("</u>"); }
    ;
    
italic: SLASH { append("<i>"); }
        (word|punctuation|escape|bold|underline|monospace|superscript|deleted|space|newline)+
        SLASH { append("</i>"); }
    ;
    
monospace: BAR { append("<tt>"); }
           (word|punctuation|escape|bold|underline|italic|superscript|deleted|space|newline)+
           BAR { append("</tt>"); }
    ;
    
superscript: HAT { append("<sup>"); }
             (word|punctuation|escape|bold|underline|italic|monospace|deleted|space|newline)+
             HAT { append("</sup>"); }
    ;
    
deleted: MINUS { append("<del>"); }
         (word|punctuation|escape|bold|underline|italic|monospace|superscript|space|newline)+
         MINUS { append("</del>"); }
    ;
    
preformatted: QUOTE { append("<pre>"); }
              (word|punctuation|specialChars|htmlSpecialChars|space|newline)*
              QUOTE { append("</pre>"); }
    ;
    
quoted: DOUBLEQUOTE { append("<quote><p>"); newLinesSinceWord=0; }
        (word|punctuation|escape|bold|underline|italic|monospace|superscript|deleted|preformatted|space|para|span)*
        DOUBLEQUOTE { append("</p></quote>"); newLinesSinceWord=0; }
    ;
 
list: olist | ulist
    ;
    
olist: para { append("<ol>\n"); } (olistItem)+ { append("</ol>\n"); }
    ;
    
olistItem: HASH { append("<li>"); newLinesSinceWord=0; }
      (word|punctuation|escape|bold|underline|italic|monospace|superscript|deleted|space)*
      { append("</li>"); } 
      para
    ;
    
ulist: para { append("<ul>\n"); } (ulistItem)+ { append("</ul>\n"); }
    ;
    
ulistItem: EQ { append("<li>"); newLinesSinceWord=0; }
      (word|punctuation|escape|bold|underline|italic|monospace|superscript|deleted|space)*
      { append("</li>"); } 
      para
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
              { newLinesSinceWord=0; } text { newLinesSinceWord=0; }
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
    