package org.jboss.seam.maven.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Element;

/**
 * 
 * Generates JSF taglib descriptor for all FacesConverter annotated classes
 * 
 * @author Rafael Benevides <https://community.jboss.org/people/rafabene>
 * @author Marek Novotny <https://community.jboss.org/people/manaRH>
 *
 */
public class ConverterGenerator
{

   private List<File> converterSources = new ArrayList<File>();
   private Log log;
   private String sourceDirectory;
   private String targetDirectory;

   public ConverterGenerator(String sourceDirectory, String targetDirectory, Log log)
   {
      this.sourceDirectory = sourceDirectory;
      this.targetDirectory = targetDirectory;
      this.log = log;
   }

   public void addFile(File file) throws FileNotFoundException
   {
      if (fileIsConverterSource(file))
      {
         converterSources.add(file);
      }
   }

   private boolean fileIsConverterSource(File file) throws FileNotFoundException
   {
      if (file.getName().endsWith(".java"))
      {
         Scanner scanner = new Scanner(file);
         String find = scanner.findWithinHorizon("@FacesConverter", 0);
         if (find != null)
         {
            log.info("Identified " + file.getName() + " as Converter source code");
            return true;
         }
      }
      return false;
   }

   public void generateConverters() throws Exception
   {
      log.info("Generating Converters");
      XMLGenerator xmlGenerator = new XMLGenerator(log);
      File outXML = new File(targetDirectory + "/generated-sources/main/resources/META-INF", "s.taglib.xml");
      List<Element> tagsToAdd = new ArrayList<Element>();
      for (File source : converterSources)
      {
         String classFromSource = source.getAbsolutePath().replace(sourceDirectory, "").replace(File.separatorChar, '.').replace(".java", "").substring(1);
         File facesConfigXML = findCorrespondentConfig(classFromSource);
         if (facesConfigXML != null)
         {
            Element tag = xmlGenerator.getFaceletsTagElementFromFacesconfig(facesConfigXML, facesConfigXML.getName().replace(".xml", ""), "converter");
            tagsToAdd.add(tag);
         }
      }
      xmlGenerator.updateFile(outXML, tagsToAdd);

   }

   private File findCorrespondentConfig(String classFromSource) throws FileNotFoundException
   {
      String whereToFind = sourceDirectory.replace("/java", "/config/component");
      log.debug("Searching correspondent config for " + classFromSource + " in " + whereToFind);
      File componentFolder = new File(whereToFind);
      for (File f : componentFolder.listFiles())
      {
         // Search only files
         if (f.isFile())
         {
            Scanner scanner = new Scanner(f);
            String find = scanner.findWithinHorizon(classFromSource, 0);
            if (find != null)
            {
               return f;
            }
         }
      }
      return null;
   }

}
