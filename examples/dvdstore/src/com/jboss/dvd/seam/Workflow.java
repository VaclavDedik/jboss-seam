/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import javax.faces.model.DataModel;

public interface Workflow {
    public String getTrack();
    public void   setTrack(String track);

    public DataModel getTasks();
    public String findTasks();
    public String viewTask();

    public String ship();
    public String loadProcess();
    public void   destroy();
}
