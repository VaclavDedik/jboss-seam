package org.jboss.seam.example.seamspace.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.mock.AbstractSeamTest.FacesRequest;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BlogTest extends JUnitSeamTest
{   
   @Deployment(name="BlogTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      EnterpriseArchive er = Deployments.seamSpaceDeployment();
      WebArchive web = er.getAsType(WebArchive.class, "seamspace-web.war");
      web.addClasses(BlogTest.class);
      return er;
   }
   
   @Test
   public void testCreateBlog() throws Exception
   {
      // Log in first
      new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            setValue("#{identity.username}", "demo");
            setValue("#{identity.password}", "demo");
            invokeAction("#{identity.login}");
            assert getValue("#{identity.loggedIn}").equals(true);
         }
      }.run();
      
      String cid = new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {         
            assert invokeAction("#{blog.createEntry}") == null;
         }         
      }.run();
      
      new FacesRequest("/createBlog.xhtml", cid)
      {
         @Override 
         protected void updateModelValues() throws Exception
         {
            setValue("#{selectedBlog.title}", "A new blog entry");
            setValue("#{selectedBlog.text}", "A very very very long section of text. " + 
                  "This text should be long enough to simulate a typical blog entry. " +
                  "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Sed interdum " +
                  "felis non arcu. Phasellus sodales pharetra dui. Suspendisse felis turpis, " +
                  "ultricies a, ullamcorper sed, nonummy id, nulla. Ut quis orci. Mauris diam " +
                  "pede, condimentum et, tempor vitae, facilisis non, sem. Mauris quam ipsum, " +
                  "laoreet non, ultricies in, aliquet nec, metus. Morbi dui. Vestibulum " +
                  "ullamcorper, tellus non hendrerit consequat, libero erat laoreet metus, " +
                  "quis facilisis arcu diam vel orci. Fusce tempor erat eget odio. Aliquam urna " +
                  "dui, dignissim id, pretium in, congue quis, est. Phasellus nec erat ac arcu " +
                  "porttitor rhoncus. Pellentesque habitant morbi tristique senectus et netus et " +
                  "malesuada fames ac turpis egestas. Nulla sed massa ut est sodales ultrices. " +
                  "Sed vitae nulla eu tellus fringilla sagittis. Nunc convallis, mi at lobortis " +
                  "rhoncus, neque turpis ullamcorper odio, quis scelerisque est dolor non velit. Integer vulputate.");
         }
         
         @Override
         protected void invokeApplication() throws Exception
         {
            assert invokeAction("#{blog.saveEntry}") == null;
         }
         
      }.run();
    
      new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            invokeAction("#{identity.logout}");
            assert getValue("#{identity.loggedIn}").equals(false);
         }
      }.run();      
   }
   
   //@Test
   public void testCreateComment() throws Exception
   {
      new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            setValue("#{identity.username}", "demo");
            setValue("#{identity.password}", "demo");
            invokeAction("#{identity.login}");
            assert getValue("#{identity.loggedIn}").equals(true);
         }
      }.run();   
      
      String cid = new FacesRequest("/comment.xhtml")
      {
         @Override
         protected void beforeRequest()
         {
            setParameter("name", "Mr_Smiley");
            setParameter("blogId", "1");
         }         

         @Override
         protected void renderResponse() throws Exception
         {
              assert getValue("#{selectedBlog}") != null;
              assert getValue("#{selectedBlog.blogId}").equals(1);
         }
      }.run();

      new FacesRequest("/comment.xhtml", cid)
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            assert invokeAction("#{blog.createComment}") == null;
            
            assert getValue("#{comment}") != null;
            assert getValue("#{comment.blog}") != null;
         }
      }.run();
      
       new FacesRequest("/comment.xhtml", cid)
       {
          @Override
          protected void updateModelValues() throws Exception
          {
             setValue("#{comment.comment}", "I totally disagree with your blog entry!");
          }
         
          @Override
          protected void invokeApplication() throws Exception
          {
             assert invokeAction("#{blog.saveComment}") == null;
          }
       }.run();
      
      new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            invokeAction("#{identity.logout}");
            assert getValue("#{identity.loggedIn}").equals(false);
         }
      }.run();    
      
   }
}
