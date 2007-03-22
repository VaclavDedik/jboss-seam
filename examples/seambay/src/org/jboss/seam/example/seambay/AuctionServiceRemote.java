package org.jboss.seam.example.seambay;

import javax.ejb.Remote;

@Remote
public interface AuctionServiceRemote
{
   Auction[] findAuctions(String searchTerm);
}
