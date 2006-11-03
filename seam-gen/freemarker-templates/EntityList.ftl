package ${packageName};

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("${componentName}List")
public class ${actionName}List extends EntityQuery
{
    @Override
    public String getEjbql() 
    { 
        return "select ${componentName} from ${actionName} ${componentName}";
    }
}
