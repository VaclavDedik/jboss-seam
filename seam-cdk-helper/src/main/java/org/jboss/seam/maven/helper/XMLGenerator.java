package org.jboss.seam.maven.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/**
 * 
 * Generates JSF taglib descriptor xml
 * 
 * @author Rafael Benevides <https://community.jboss.org/people/rafabene>
 * @author Marek Novotny <https://community.jboss.org/people/manaRH>
 *
 */
public class XMLGenerator
{

   private Log log;

   public XMLGenerator(Log log)
   {
      this.log = log;
   }

   public Element getFaceletsTagElementFromFacesconfig(File xml, String tagName, String converterOrValidator) throws Exception
   {
      log.info("Generating taglib from " + xml);

      DocumentBuilder dstDB = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      DocumentBuilder srcDB = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document destDocument = dstDB.newDocument();
      Document srcDocument = srcDB.parse(xml);

      XPath xpath = XPathFactory.newInstance().newXPath();

      Element tagElement = destDocument.createElement("tag");
      Node tagNode = destDocument.appendChild(tagElement);
      Element tagNameElement = destDocument.createElement("tag-name");
      tagNameElement.setTextContent(tagName);
      tagNode.appendChild(tagNameElement);

      Element component = destDocument.createElement(converterOrValidator);
      Node componentNode = tagNode.appendChild(component);

      Element description = destDocument.createElement("description");
      String descriptionContent = xpath.evaluate("//" + converterOrValidator + "/description/text()", srcDocument);
      description.setTextContent(descriptionContent);

      componentNode.appendChild(description);

      Element componentType = destDocument.createElement(converterOrValidator + "-id");
      String componentTypeContent = xpath.evaluate("//" + converterOrValidator + "-id/text()", srcDocument);
      componentType.setTextContent(componentTypeContent);

      componentNode.appendChild(componentType);

      NodeList propertyNodes = (NodeList) xpath.evaluate("//property", srcDocument, XPathConstants.NODESET);

      for (int i = 0; i < propertyNodes.getLength(); i++)
      {
         Node nNode = propertyNodes.item(i);
         if (nNode.getNodeType() == Node.ELEMENT_NODE)
         {

            Element eElement = (Element) nNode;
            String propDescription = getTagValue("description", eElement);
            String propName = getTagValue("property-name", eElement);
            String propClass = getTagValue("property-class", eElement);

            Element attribute = destDocument.createElement("attribute");
            Element atributeDescription = destDocument.createElement("description");
            atributeDescription.setTextContent(propDescription);
            attribute.appendChild(atributeDescription);

            Element attributeName = destDocument.createElement("name");
            attributeName.setTextContent(propName);
            attribute.appendChild(attributeName);

            Element attributeClass = destDocument.createElement("type");
            attributeClass.setTextContent(propClass);
            attribute.appendChild(attributeClass);

            tagElement.appendChild(attribute);
         }

      }

      return tagElement;

   }

   private static String getTagValue(String sTag, Element eElement)
   {
      NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

      Node nValue = (Node) nlList.item(0);

      return nValue.getNodeValue();
   }

   private void writeFileContent(File outXML, String content) throws IOException
   {
      log.info("Updating " + outXML);
      FileOutputStream fos = new FileOutputStream(outXML);
      fos.write(content.getBytes());

   }

   public void updateFile(File outXML, List<Element> tags) throws Exception
   {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.parse(outXML);

      for (Element tag : tags)
      {
         Comment comment = doc.createComment("Converter added by seam-cdk-helper plugin");
         Node firstchild = doc.getFirstChild();
         firstchild.appendChild(comment);
         firstchild.appendChild(doc.importNode(tag, true));
      }

      // ///////////////
      // Output the XML
      TransformerFactory tf = TransformerFactory.newInstance();
      tf.setAttribute("indent-number", new Integer(4));

      Transformer transformer = tf.newTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

      DOMImplementation domImplementation = doc.getImplementation();
      if (domImplementation.hasFeature("LS", "3.0") && domImplementation.hasFeature("Core", "2.0"))
      {
         DOMImplementationLS domImplementationLS = (DOMImplementationLS) domImplementation.getFeature("LS", "3.0");
         LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
         DOMConfiguration domConfiguration = lsSerializer.getDomConfig();
         if (domConfiguration.canSetParameter("format-pretty-print", true))
         {
            lsSerializer.getDomConfig().setParameter("format-pretty-print", true);
            LSOutput lsOutput = domImplementationLS.createLSOutput();
            lsOutput.setEncoding("UTF-8");
            StringWriter stringWriter = new StringWriter();
            lsOutput.setCharacterStream(stringWriter);
            lsSerializer.write(doc, lsOutput);
            writeFileContent(outXML, stringWriter.toString());
         }
         else
         {
            throw new RuntimeException("DOMConfiguration 'format-pretty-print' parameter isn't settable.");
         }
      }
      else
      {
         throw new RuntimeException("DOM 3.0 LS and/or DOM 2.0 Core not supported.");
      }
   }
}
