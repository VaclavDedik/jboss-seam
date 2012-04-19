package org.jboss.seam.example.seambay.test;

import java.io.File;
import java.util.List;

import javax.faces.model.DataModel;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.example.seambay.Auction;
import org.jboss.seam.example.seambay.Category;
import org.jboss.seam.mock.AbstractSeamTest.FacesRequest;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class AuctionTest extends JUnitSeamTest
{
   @Deployment(name="AuctionTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      EnterpriseArchive ear = ShrinkWrap.create(ZipImporter.class, "seam-seambay.ear").importFrom(new File("../seambay-ear/target/seam-seambay.ear")).as(EnterpriseArchive.class);

      // Install org.jboss.seam.mock.MockSeamListener
      WebArchive web = ear.getAsType(WebArchive.class, "seambay-web.war");
      web.delete("/WEB-INF/web.xml");
      web.addAsWebInfResource("web.xml");
      
      web.addClasses(AuctionTest.class);

      return ear;
   }
   
   @Test
   public void testCreateAuction() throws Exception
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
      
      String cid = new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            invokeAction("#{auctionAction.createAuction}");
         }
         
         @Override
         protected void renderResponse()
         {
            Auction auction = (Auction) getValue("#{auctionAction.auction}");
            assert auction != null;
         }
      }.run();
            
      new FacesRequest("/sell.xhtml", cid)
      {
         @Override 
         protected void updateModelValues() throws Exception
         {
            setValue("#{auctionAction.auction.title}", "A Widget");
         }
      }.run();
      
      
      new FacesRequest("/sell2.xhtml", cid)
      {
         @Override 
         protected void updateModelValues() throws Exception
         {
            List<Category> categories = (List<Category>) getValue("#{allCategories}");
            
            setValue("#{auctionAction.auction.category}", categories.get(0));
         }
      }.run();      
      
      new FacesRequest("/sell3.xhtml", cid)
      {
         @Override 
         protected void updateModelValues() throws Exception
         {
            setValue("#{auctionAction.duration}", 3);
            setValue("#{auctionAction.auction.startingPrice}", 100.0);
         }
         
      }.run();
      
      new FacesRequest("/sell5.xhtml", cid)
      {
         @Override 
         protected void updateModelValues() throws Exception
         {
            setValue("#{auctionAction.auction.description}", "foo");
         }         
      }.run();      
      
      new FacesRequest("/preview.xhtml", cid)
      {
         @Override 
         protected void invokeApplication() throws Exception
         {
            Auction auction = (Auction) getValue("#{auctionAction.auction}");
            invokeAction("#{auctionAction.confirm}");
            assert auction.getStatus() == Auction.STATUS_LIVE;
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
 
   @Test
   public void testBidding() throws Exception
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
            
      new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            invokeAction("#{auctionAction.createAuction}");
            setValue("#{auctionAction.auction.title}", "BidTestZZZ");
            setValue("#{auctionAction.auction.startingPrice}", 1);         
            setValue("#{auctionAction.auction.description}", "bar");
            setValue("#{auctionAction.categoryId}", 1001);
            
            Auction auction = (Auction) getValue("#{auctionAction.auction}"); 

            assert auction.getStatus() == Auction.STATUS_UNLISTED;
            
            invokeAction("#{auctionAction.confirm}");
            
            assert auction.getStatus() == Auction.STATUS_LIVE;            
            assert auction.getHighBid() == null;
         }
      }.run();      
      
      new FacesRequest()
      {
         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{auctionSearch.searchTerm}", "BidTestZZZ");
         }
         
         @Override
         protected void invokeApplication() throws Exception
         {
            assert invokeAction("#{auctionSearch.queryAuctions}") == null;
         }
         
         @Override
         protected void renderResponse() throws Exception
         {
            DataModel auctions = (DataModel) Contexts.getSessionContext().get("auctions");
            assert auctions.getRowCount() == 1;
            Auction auction = ((Auction) auctions.getRowData()); 
            assert auction.getTitle().equals("BidTestZZZ");
            assert auction.getHighBid() == null;
         }
         
      }.run();
         
      new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            Contexts.getEventContext().set("auction", getValue("#{auctionSearch.auctions[0]}"));
            
            assert invokeAction("#{bidAction.placeBid}") == null;
            assert getValue("#{bidAction.outcome}").equals("required");
            Contexts.getEventContext().set("bidAmount", "5.00");
            
            assert invokeAction("#{bidAction.placeBid}") == null;
            assert getValue("#{bidAction.outcome}").equals("confirm");
            
            assert invokeAction("#{bidAction.confirmBid}").equals("success");
         }
      }.run();
      
      new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            assert invokeAction("#{auctionSearch.queryAuctions}") == null;
         }
         
         protected void renderResponse() throws Exception
         {
            DataModel auctions = (DataModel) Contexts.getSessionContext().get("auctions");
            Auction auction = ((Auction) auctions.getRowData());
            assert auction.getHighBid() != null;
         }
      }.run();
      
      
   }
   
}
