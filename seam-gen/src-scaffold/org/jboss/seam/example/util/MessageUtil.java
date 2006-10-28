package org.jboss.seam.example.util;

import org.jboss.seam.core.FacesMessages;

public class MessageUtil {
	
	public static void addMsg(String msg) {
		FacesMessages.instance().addFromResourceBundle(msg);		
	}
}
