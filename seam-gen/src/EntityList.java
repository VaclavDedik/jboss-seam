package @actionPackage@;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import @modelPackage@.@entityName@;

@Name("@listName@")
public class @entityName@List extends EntityQuery<@entityName@>
{
    @Override
    public String getEjbql()
    {
        return "select @componentName@ from @entityName@ @componentName@";
    }
}
