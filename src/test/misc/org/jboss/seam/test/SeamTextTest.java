package org.jboss.seam.test;

import java.io.InputStreamReader;
import java.io.Reader;

import org.jboss.seam.text.L;
import org.jboss.seam.text.P;

public class SeamTextTest
{
    public static void main(String[] args) throws Exception {
        Reader r = new InputStreamReader( SeamTextTest.class.getResourceAsStream("SeamTextTest.txt") );
        L lexer = new L(r);
        P parser = new P(lexer);
        parser.startRule();
        System.out.println(parser);
    }
}
