package org.jboss.seam.example.bpm;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Name;

@Name("cartBean")
public class ShoppingCartBean {
	
	public List<String> items;
	
	public ShoppingCartBean(){
		items = new ArrayList();
		items.add("Car");
		items.add("Boat");
		items.add("Truck");
	}

	public List getItems() {
		return items;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}	
}
