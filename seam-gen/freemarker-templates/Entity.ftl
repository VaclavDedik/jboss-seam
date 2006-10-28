package ${entityPackage};

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.jboss.seam.annotations.Name;

@Entity
@Name("${actionName}")
public class ${actionName} implements Serializable {
	
	//seam-gen attributes (you should probably edit these)
	private String column1;
	private String column2;
	
    //add additional entity attributes
	
	//seam-gen attribute getters/setters with annotations (you probably should edit)
		
	@Id 	   
	public String getColumn1() {
	     return column1;
	}

	public void setColumn1(String column1) {
	     this.column1 = column1;
	}
	
	public String getColumn2() {
	     return column2;
	}

	public void setColumn2(String column2) {
	     this.column2 = column2;
	}   	
}
