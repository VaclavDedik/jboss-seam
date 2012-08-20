package org.jboss.seam.maven.helper;


import java.io.File;
import java.io.FileNotFoundException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * 
 * Goal which appends validator and converter custom tag file into face.
 * 
 * @goal execute
 * 
 * @phase generate-sources
 * 
 * @author Rafael Benevides <https://community.jboss.org/people/rafabene>
 * @author Marek Novotny <https://community.jboss.org/people/manaRH>
 *
 */
public class SeamGeneratorValidatorAndConvertersMojo extends AbstractMojo
{
   /**
    * The source directories containing the sources to be compiled.
    * 
    * @parameter expression="${project.build.sourceDirectory}"
    * @required
    * @readonly
    */
   protected String sourceDirectory;

   /**
    * Output directory for processed resources
    * 
    * @parameter expression="${project.build.directory}"
    * @required
    */
   private String targetDirectory;

   private ConverterGenerator converterGenerator;
   private ValidatorGenerator validatorGenerator;

   public void execute() throws MojoExecutionException
   {
      converterGenerator = new ConverterGenerator(sourceDirectory, targetDirectory, getLog());
      validatorGenerator = new ValidatorGenerator(targetDirectory, getLog());
      try
      {
         File sourceFolder = new File(sourceDirectory);
         getLog().info("Source Folder: " + sourceFolder);
         visitFolder(sourceFolder);
         converterGenerator.generateConverters();
         visitFolder(new File(sourceFolder.getParent(), "config/component"));
         validatorGenerator.generateValidators();
      }
      catch (Exception e)
      {
         throw new MojoExecutionException("Error on Generator", e);
      }

   }

   private void visitFolder(File sourceFolder) throws FileNotFoundException
   {
      for (File file : sourceFolder.listFiles())
      {
         if (file.isDirectory())
         {
            visitFolder(file);
         }
         else
         {
            converterGenerator.addFile(file);
            validatorGenerator.addFile(file);
         }
      }
   }
}
