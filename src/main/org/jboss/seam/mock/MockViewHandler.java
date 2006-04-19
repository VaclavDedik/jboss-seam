package org.jboss.seam.mock;

import java.io.IOException;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

public class MockViewHandler extends ViewHandler {

	@Override
	public Locale calculateLocale(FacesContext ctx) {
		return Locale.getDefault();
	}

	@Override
	public String calculateRenderKitId(FacesContext ctx) {
		return null;
	}

	@Override
	public UIViewRoot createView(FacesContext ctx, String viewId) {
		return null;
	}

	@Override
	public String getActionURL(FacesContext ctx, String viewId) {
		return viewId;
	}

	@Override
	public String getResourceURL(FacesContext ctx, String url) {
		return url;
	}

	@Override
	public void renderView(FacesContext ctx, UIViewRoot viewRoot)
			throws IOException, FacesException {
	}

	@Override
	public UIViewRoot restoreView(FacesContext ctx, String id) {
		return null;
	}

	@Override
	public void writeState(FacesContext ctx) throws IOException {
	}

}
