package @actionPackage@;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("@listName@")
public class @entityName@List extends EntityQuery
{
    @Override
    public String getEjbql() 
    { 
        return "select @componentName@ from @entityName@ @componentName@";
    }
}
