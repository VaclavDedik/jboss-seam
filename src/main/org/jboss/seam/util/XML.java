package org.jboss.seam.util;

import java.io.InputStream;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XML
{
   public static Element getRootElement(InputStream stream) throws DocumentException
   {
      SAXReader saxReader = new SAXReader();
      saxReader.setEntityResolver(new DTDEntityResolver());
      saxReader.setMergeAdjacentText(true);
      return saxReader.read(stream).getRootElement();
   }

}
