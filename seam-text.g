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
    private StringBuilder builder = new StringBuilder();
    
    public String toString() {
        return builder.toString();
    }
    
    public void append(String... strings) {
        for (String string: strings) builder.append(string);
    }   
}

startRule: { append("<p>\n"); }
           text
           { append("\n</p>\n"); }
    ;

text: (word|punctuation|formatting|escape|ws|para|span)*
    ;
    
formatting: bold|underline|italic|monospace|superscript|deleted|preformatted|quoted
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
      (word|punctuation|escape|underline|italic|monospace|superscript|deleted|ws)*
      STAR { append("</b>"); }
    ;
    
underline: UNDERSCORE { append("<u>"); }
           (word|punctuation|escape|bold|italic|monospace|superscript|deleted|ws)*
           UNDERSCORE { append("</u>"); }
    ;
    
italic: SLASH { append("<i>"); }
        (word|punctuation|escape|bold|underline|monospace|superscript|deleted|ws)*
        SLASH { append("</i>"); }
    ;
    
monospace: BAR { append("<tt>"); }
           (word|punctuation|escape|bold|underline|italic|superscript|deleted|ws)*
           BAR { append("</tt>"); }
    ;
    
superscript: HAT { append("<sup>"); }
             (word|punctuation|escape|bold|underline|italic|monospace|deleted|ws)*
             HAT { append("</sup>"); }
    ;
    
deleted: MINUS { append("<del>"); }
         (word|punctuation|escape|bold|underline|italic|monospace|superscript|ws)*
         MINUS { append("</del>"); }
    ;
    
preformatted: QUOTE { append("<pre>"); }
              (word|punctuation|specialChars|htmlSpecialChars|ws)*
              QUOTE { append("</pre>"); }
    ;
    
quoted: DOUBLEQUOTE { append("<quote>"); }
        (word|punctuation|escape|bold|underline|italic|monospace|superscript|deleted|preformatted|ws|para|span)*
        DOUBLEQUOTE { append("</quote>"); }
    ;
    
ws: WS { append(" "); }
    ;
        
para: NEWLINE NEWLINE { append("\n</p>\n<p>\n"); }
    ;

span: LT tag:WORD { append("<" + tag.getText()); } 
          (
              ws att:WORD EQ 
              DOUBLEQUOTE { append(att.getText() + "=\""); } 
              attributeValue 
              DOUBLEQUOTE { append("\""); } 
          )* 
      (
          (
              GT { append(">"); } 
              text 
              LT SLASH WORD GT { append("</" + tag.getText() + ">"); }
          )
      |   (
              SLASH GT { append("/>"); } 
          )
      )
    ;
    
attributeValue: ( AMPERSAND { append("&amp;"); } | word | punctuation | ws | specialChars )*
    ;

class L extends Lexer;
options
{
	k=2;
}

WORD: ('a'..'z'|'A'..'Z'|'0'..'9')+
    ;
    
PUNCTUATION: ':' | ';' | '(' | ')' | '?' | '!' | '@' | '%' | '.'
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
    
WS: (' ' | '\t')+
    ;

NEWLINE: '\r' '\n'   // DOS
    |    '\n'        // UNIX
    |    '\r'        // Mac
    ;
