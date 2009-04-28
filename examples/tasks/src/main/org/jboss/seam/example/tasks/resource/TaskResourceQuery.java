/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.seam.example.tasks.resource;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.tasks.ResourceNotFoundException;
import org.jboss.seam.example.tasks.entity.Task;
import org.jboss.seam.resteasy.ResourceQuery;

/**
 * This class exposes two lists of tasks for every context. Unresolved tasks can
 * be obtained by sending HTTP GET request to context/{context}/unresolved URI.
 * To obtain a list of already resolved tasks, use context/{context}/resolved
 * URI.
 * 
 * @author Jozef Hartinger
 * 
 */
@Name("taskResourceQuery")
@Path("/auth/context/{context}/{status}")
public class TaskResourceQuery extends ResourceQuery<Task>
{

   @PathParam("context")
   private String contextName;
   @PathParam("status")
   private String taskStatus;

   public TaskResourceQuery()
   {
      setMediaTypes(new String[] { "application/xml", "application/json", "application/fastinfoset" });
   }

   @Override
   @Create
   public void create()
   {
      super.create();
      List<String> restrictions = new ArrayList<String>();
      restrictions.add("context.name = #{contextName}");
      restrictions.add("resolved = #{taskStatus}");
      restrictions.add("context.owner.username = #{user.username}");
      getEntityQuery().setRestrictionExpressionStrings(restrictions);
   }

   @Factory("contextName")
   public String getContextName()
   {
      return contextName;
   }

   @Factory("taskStatus")
   public boolean isResolved()
   {
      if (taskStatus.equals("resolved"))
      {
         return true;
      }
      else if (taskStatus.equals("unresolved"))
      {
         return false;
      }
      else
      {
         throw new ResourceNotFoundException();
      }
   }
}
