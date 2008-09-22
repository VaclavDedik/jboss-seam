package org.jboss.seam.excel;

import java.util.LinkedList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("rssTest")
@Scope(ScopeType.SESSION)
public class RSSTest
{
   private Feed feed;
   private List<Entry> entries;

   @Create
   public void create()
   {
      feed = new Feed();
      entries = new ArrayList<Entry>();
      for (int i = 0; i < 5; i++) {
         Entry entry = new Entry();
         entires.add(entry);
      }
   }
   
   public Feed getFeed() {
      return feed;
   }
   
   public List<Entry> getEntries() {
      return entries;
   }

}
