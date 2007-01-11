package org.jboss.seam.example.seamspace;

import javax.ejb.Local;

@Local
public interface CommentLocal
{
  void destroy();
}
