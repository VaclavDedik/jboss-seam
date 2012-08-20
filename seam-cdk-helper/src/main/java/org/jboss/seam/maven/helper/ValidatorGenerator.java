package org.jboss.seam.maven.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Element;

/**
 * 
 * Generates JSF taglib descriptor for all Validators classes
 * 
 * @author Rafael Benevides <https://community.jboss.org/people/rafabene>
 * @author Marek Novotny <https://community.jboss.org/people/manaRH>
 *
 */
public class ValidatorGenerator
{

   private List<File> validatorXMLs = new ArrayList<File>();
   private Log log;
   private String targetDirectory;
   private Map<String, String> validatorNames = new HashMap<String, String>();

   public ValidatorGenerator(String targetDirectory, Log log)
   {
      this.targetDirectory = targetDirectory;
      this.log = log;
      validatorNames.put("formattedTextValidator.xml", "validateFormattedText");
      validatorNames.put("modelValidator.xml", "validate");
   }

   public void addFile(File file) throws FileNotFoundException
   {
      if (fileIsValidatorXML(file))
      {
         validatorXMLs.add(file);
      }
   }

   private boolean fileIsValidatorXML(File file) throws FileNotFoundException
   {
      if (file.getName().endsWith(".xml"))
      {
         Scanner scanner = new Scanner(file);
         String find = scanner.findWithinHorizon("<validator>", 0);
         if (find != null)
         {
            log.info("Identified " + file.getName() + " as Validator XML");
            return true;
         }
      }
      return false;
   }

   public void generateValidators() throws Exception
   {
      log.info("Generating Validators");
      XMLGenerator xmlGenerator = new XMLGenerator(log);
      File outXML = new File(targetDirectory + "/generated-sources/main/resources/META-INF", "s.taglib.xml");
      List<Element> tagsToAdd = new ArrayList<Element>();
      for (File xml : validatorXMLs)
      {
         Element tag = xmlGenerator.getFaceletsTagElementFromFacesconfig(xml, validatorNames.get(xml.getName()), "validator");
         tagsToAdd.add(tag);
      }
      xmlGenerator.updateFile(outXML, tagsToAdd);
   }

}
