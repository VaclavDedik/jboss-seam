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
    private boolean bold = false;
    private boolean underline = false;
    private boolean italic = false;
    private boolean deleted = false;
    private boolean monospace = false;
    private boolean preformatted = false;
    private boolean quoted = false;
    private boolean superscript = false;
    
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

text: (word|punctuation|escape|bold|underline|italic|monospace|superscript|deleted|preformatted|quoted|ws|para|span)*
    ;

word: w:WORD { append( w.getText() ); }
    ;
    
punctuation: p:PUNCTUATION { append( p.getText() ); }
    ;
    
escape: ESCAPE ( seam | html )
    ;
    
seam: { Token t=null; } 
      ( 
          st:STAR {t=st;} 
        | sl:SLASH {t=sl;} 
        | b:BAR {t=b;} 
        | h:HAT {t=h;} 
        | q:QUOTE {t=q;} 
        | m:MINUS {t=m;} 
        | p:PLUS {t=p;} 
        | eq:EQ {t=eq;}
      )
      { append( t.getText() ); }
    ;


html: GT { append("&gt;"); } 
    | LT { append("&lt;"); } 
    | DOUBLEQUOTE { append("&quot;"); } 
    | AMPERSAND { append("&amp;"); }
    ;
    
bold: STAR { 
    bold = !bold;
    if (bold) append("<b>"); else append("</b>");
    }
    ;
    
underline: UNDERSCORE { 
    underline = !underline;
    if (underline) append("<u>"); else append("</u>");
    }
    ;
    
italic: SLASH { 
    italic = !italic;
    if (italic) append("<i>"); else append("</i>");
    }
    ;
    
monospace: BAR { 
    monospace = !monospace;
    if (monospace) append("<tt>"); else append("</tt>");
    }
    ;
    
superscript: HAT { 
    superscript = !superscript;
    if (superscript) append("<sup>"); else append("</sup>");
    }
    ;
    
preformatted: QUOTE { 
    preformatted = !preformatted;
    if (preformatted) append("<pre>"); else append("</pre>");
    }
    ;
    
quoted: DOUBLEQUOTE { 
    quoted = !quoted;
    if (quoted) append("<quote>"); else append("</quote>");
    }
    ;
    
deleted: MINUS { 
    deleted = !deleted;
    if (deleted) append("<del>"); else append("</del>");
    }
    ;
    
ws: WS { append(" "); }
    ;
        
para: NEWLINE NEWLINE { 
    append("\n</p>\n<p>\n"); 
    bold = false;
    underline = false;
    italic = false;
    deleted = false;
    monospace = false;
    superscript = false;
    }
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
    
attributeValue: ( word | punctuation | ws | seam )*
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
