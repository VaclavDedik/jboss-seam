<!DOCTYPE suite SYSTEM "http://beust.com/testng/testng-1.0.dtd" >

<suite name="${actionName} Tests" verbose="2" parallel="false">
   <test name="${actionName} Test">
     <classes>
       <class name="${testPackageName}.${actionName}Test"/>       
     </classes>
   </test>	
</suite>