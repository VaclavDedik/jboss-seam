/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import java.util.ArrayList;
import java.util.Iterator;

import javax.faces.context.ExternalContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class FacesApplicationContext implements Context {

	private ExternalContext externalContext;
	
   public ScopeType getType()
   {
      return ScopeType.APPLICATION;
   }

   private String getKey(String name)
   {
      return /*getPrefix() +*/ name;
   }

   private String getPrefix()
   {
      return ScopeType.APPLICATION.getPrefix() + '$';
   }

	public FacesApplicationContext(ExternalContext externalContext) {
		this.externalContext = externalContext;
	}

	public Object get(String name) {
		return externalContext.getApplicationMap().get( getKey(name) );
	}

	public void set(String name, Object value) {
       externalContext.getApplicationMap().put( getKey(name), value );
	}

	public boolean isSet(String name) {
		return get(name)!=null;
	}

	public void remove(String name) {
       externalContext.getApplicationMap().remove( getKey(name) );
	}

    public String[] getNames() {
       Iterator names = externalContext.getApplicationMap().keySet().iterator();
       ArrayList<String> results = new ArrayList<String>();
       //String prefix = getPrefix();
       while ( names.hasNext() )
       {
          String name = (String) names.next();
          /*if ( name.startsWith(prefix) )
          {
             results.add( name.substring(prefix.length()) );
          }*/
          results.add(name);
       }
       return results.toArray(new String[]{});
    }

   public Object get(Class clazz)
   {
      return get( Seam.getComponentName(clazz) );
   }

   public void flush() {}

}
