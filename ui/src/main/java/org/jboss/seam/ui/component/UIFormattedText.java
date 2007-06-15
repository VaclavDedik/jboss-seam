/**
 * License Agreement.
 *
 * Ajax4jsf 1.1 - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package org.jboss.seam.ui.component;

import java.io.Reader;
import java.io.StringReader;

import javax.faces.component.UIOutput;

import org.jboss.seam.text.SeamTextLexer;
import org.jboss.seam.text.SeamTextParser;

import antlr.ANTLRException;

/**
 * JSF component class
 *
 */
public abstract class UIFormattedText extends UIOutput {
	
	@SuppressWarnings("unused")
   private static final String COMPONENT_TYPE = "org.jboss.seam.ui.FormattedText";
	
	@SuppressWarnings("unused")
   private static final String COMPONENT_FAMILY = "org.jboss.seam.ui.FormattedText";
   
   public String getFormattedText() 
   {
      if ( getValue() == null) return null;
      Reader r = new StringReader( (String) getValue() );
      SeamTextLexer lexer = new SeamTextLexer(r);
      SeamTextParser parser = new SeamTextParser(lexer);
      try
      {
         parser.startRule();
      }
      catch (ANTLRException re)
      {
         throw new RuntimeException(re);
      }
      return parser.toString();
   }
	
}
