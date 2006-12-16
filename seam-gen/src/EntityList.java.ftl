<#assign entityName = pojo.shortName>
<#assign componentName = util.lower(entityName)>
<#assign listName = componentName + "List">
${pojo.packageDeclaration}

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("${listName}")
public class ${entityName}List extends EntityQuery
{

    private static final String[] RESTRICTIONS = {
<#foreach property in pojo.allPropertiesIterator>
<#if !c2h.isCollection(property) && !c2h.isManyToOne(property)>
<#if property.value.typeName == "string">
        "lower(${componentName}.${property.name}) like concat(lower(${'#'}{${listName}.${componentName}.${property.name}}),'%')",
</#if>
</#if>
</#foreach>
    };

    private ${entityName} ${componentName} = new ${entityName}();

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
    
    public ${entityName} get${entityName}()
    {
        return ${componentName};
    }
    
    @Override
    public List<String> getRestrictions()
    {
        return Arrays.asList(RESTRICTIONS);
    }

}
