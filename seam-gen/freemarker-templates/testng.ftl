<!DOCTYPE suite SYSTEM "http://beust.com/testng/testng-1.0.dtd" >

<suite name="${actionName}" verbose="2" parallel="false">
   <test name="${actionName}">
     <classes>
       <class name="${testPackageName}.${actionName}"/>       
     </classes>
   </test>	
</suite>