package org.jboss.seam.core;
import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
/**
 * @author Gavin King
 * @version $Revision$
 */
@Scope(ScopeType.PAGE)
@Name("org.jboss.seam.core.conversationList")
@Install(precedence=BUILT_IN)
@Intercept(NEVER)
public class ConversationList implements Serializable {
   
   private static final long serialVersionUID = -1515889862229134356L;
   private List<ConversationEntry> conversationEntryList;
   
   @Create
   public void createConversationEntryList()
   {
      ConversationEntries conversationEntries = ConversationEntries.instance();
      Set<ConversationEntry> orderedEntries = new TreeSet<ConversationEntry>();
      orderedEntries.addAll( conversationEntries.getConversationEntries() );
      conversationEntryList = new ArrayList<ConversationEntry>( conversationEntries.size() );
      for ( ConversationEntry entry: orderedEntries )
      {
         if ( entry.isDisplayable() && !Seam.isSessionInvalid() )
         {
            conversationEntryList.add(entry);
         }
      }
   }
   
   @Unwrap
   public List<ConversationEntry> getConversationEntryList()
   {
      return conversationEntryList;
   }
}
