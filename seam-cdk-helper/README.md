Seam CDK Helper
=============
Author: Rafael Benevides

Maven Plugin to generate Validators and Converters for Seam 2.3 - Workaround for Richfaces 4 CDK

Why this Maven Plugin was created?
----------------------------------

The converters and validators wasn't available as related at https://issues.jboss.org/browse/JBSEAM-4955

There is a related issue with Richfaces 4 the prevents this generation https://issues.jboss.org/browse/RF-12271

This plugin generates the conveters by looking for classes annotated with @FacesConvertes and the looks for its configs to generate its tag on s.taglib.xml

For validators the source of the information is the correspondent xml files


How to use this plugin ?
-----------------------

Just at the plugin information on jboss-seam-ui pom.xml


    <build>
        <plugins>
          ...
          <!-- This need to be after Richfaces CDK plugin -->
          <plugin>
             <groupId>org.jboss.seam</groupId>
        		 <artifactId>seam-cdk-helper</artifactId>
        		 <version>1.0</version>
        		 <executions>
                <execution>
                   <phase>generate-sources</phase>
                   <goals>
                      <goal>execute</goal>
                   </goals>
                </execution>
             </executions>
          </plugin>
          ...

