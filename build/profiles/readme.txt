This directory contains "dependency profiles" for use with Seam.

The profiles provide functional dependencies (for example choose between 
seam-security-all, seam-security-rules and/or seam-security-captcha OR 
seam-ui-facelets, seam-ui-jsp).  They are a more concise way of expressing 
your dependencies but with less control for you.

For example if you wanted your application to use Seam, Seam UI with Facelets, 
Seam Security with JCaptcha and Drools support then you would use these profiles:

   groupId: org.jboss.seam.profile.functional
      artifactId: seam-ui-facelets
      artifactId: seam-security-all
   
vs:
   
   groupId: org.drools
      artifactId: drools-core
      artifactId: drools-compiler
   groupId: com.octo.captcha
      artifactId: jcaptcha-all
   groupId: com.sun.facelets
      artifactId: jsf-facelets
   groupId: org.jboss.seam
      artifactId: jboss-seam-ui
      artifactId: jboss-seam-core
   
These profiles are work in progress.  If you have others you would like to 
include in Seam, please submit them as patches to JIRA.