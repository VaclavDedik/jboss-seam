package org.jboss.seam.example.poker;

import org.jboss.seam.annotations.WebRemote;

/**
 * Local interface for player actions
 *
 * @author Shane Bryzak
 */
public interface PlayerLocal
{
  @WebRemote
  boolean login(String playerName);
}
