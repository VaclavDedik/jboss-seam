package org.jboss.seam.example.seamspace;

import javax.ejb.Local;

@Local
public interface ContentLocal
{
  MemberImage getImage(int imageId);
}
