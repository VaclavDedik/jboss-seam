package org.jboss.seam.drools;
import static org.jboss.seam.InterceptionType.NEVER;
import java.io.InputStreamReader;
import java.io.Reader;
import org.drools.RuleBaseFactory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.lang.descr.PackageDescr;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.util.Resources;
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
public class RuleBase
{
   private String[] ruleFiles;
   private String dslFile;
   private org.drools.RuleBase ruleBase;
   
   @Create
   public void compileRuleBase() throws Exception
   {
      PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
      conf.setCompiler(PackageBuilderConfiguration.JANINO);
      PackageBuilder builder = new PackageBuilder(conf);
      
      if (ruleFiles!=null)
      {
         for (String ruleFile: ruleFiles)
         {
            // read in the source
            Reader drlReader = new InputStreamReader( Resources.getResourceAsStream(ruleFile) );
            PackageDescr packageDescr;
            if (dslFile==null)
            {
               packageDescr = new DrlParser().parse(drlReader);
            }
            else
            {
               Reader dslReader = new InputStreamReader( Resources.getResourceAsStream(dslFile) );
               packageDescr = new DrlParser().parse(drlReader, dslReader);
            }
            // pre build the package
            builder.addPackage(packageDescr);
         }
      }
      
      // add the package to a rulebase
      ruleBase = RuleBaseFactory.newRuleBase();
      ruleBase.addPackage( builder.getPackage() );
   }
   
   @Unwrap
   public org.drools.RuleBase getRuleBase()
   {
      return ruleBase;
   }
   public String[] getRuleFiles()
   {
      return ruleFiles;
   }
   public void setRuleFiles(String[] ruleFiles)
   {
      this.ruleFiles = ruleFiles;
   }
   public String getDslFile()
   {
      return dslFile;
   }
   public void setDslFile(String dslFile)
   {
      this.dslFile = dslFile;
   }
}
