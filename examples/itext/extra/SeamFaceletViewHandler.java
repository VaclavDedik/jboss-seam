/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.seam.ui.facelet;

import javax.faces.FacesException;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.application.ViewHandler;

import com.sun.facelets.FaceletViewHandler;
import com.sun.facelets.compiler.Compiler;

import java.io.IOException;



/**
 * This class just extends the FaceletViewHandler to set the ExpressionFactory
 * to the Seam version.
 *
 * @author Stan Silvert
 */
public class SeamFaceletViewHandler extends FaceletViewHandler 
{
    private static final String SEAM_EXPRESSION_FACTORY = "org.jboss.seam.ui.facelet.SeamExpressionFactory";
    
    public SeamFaceletViewHandler(ViewHandler parent)
    {
        super(parent);
    }
    
    @Override
    protected Compiler createCompiler() 
    {
        Compiler compiler = super.createCompiler();
        compiler.setFeature(Compiler.EXPRESSION_FACTORY, SEAM_EXPRESSION_FACTORY);
        return compiler;
    }


    public void myBuildView(FacesContext context, UIViewRoot viewToRender)
        throws IOException, FacesException 
    {        
        initialize(context); // shouldn't have to do this

        System.out.println("Building: " + viewToRender + " -> " + viewToRender.getChildCount());
        buildView(context, viewToRender);
        System.out.println("Built: "    + viewToRender + " -> " + viewToRender.getChildCount());
    }

}
