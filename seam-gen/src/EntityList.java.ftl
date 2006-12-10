<#assign entityName = pojo.shortName>
<#assign componentName = util.lower(entityName)>
<#assign listName = componentName + "List">
${pojo.packageDeclaration}

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("${listName}")
public class ${entityName}List extends EntityQuery
{

    @Override
    public String getEjbql() 
    { 
        return "select ${componentName} from ${entityName} ${componentName}";
    }
    
    @Override
    public Integer getMaxResults()
    {
    	return 25;
    }
    
}
