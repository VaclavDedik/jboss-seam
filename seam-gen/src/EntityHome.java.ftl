<#assign entityName = pojo.shortName>
<#assign componentName = util.lower(entityName)>
<#assign homeName = componentName + "Home">
${pojo.packageDeclaration}

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.framework.EntityHome;

import java.util.List;
import java.util.ArrayList;

@Name("${homeName}")
public class ${entityName}Home extends EntityHome<${entityName}>
{

    @RequestParameter 
    ${pojo.identifierProperty.type.returnedClass.name} ${componentName}Id;
    
    @Override
    public Object getId() 
    { 
        if (${componentName}Id==null)
        {
            return super.getId();
        }
        else
        {
            return ${componentName}Id;
        }
    }
    
    @Override @Begin(join=true)
    public void create() {
        super.create();
    }
 	
<#foreach property in pojo.allPropertiesIterator>
<#assign getter = "get" + pojo.getPropertyName(property)>
<#if c2h.isOneToManyCollection(property)>
    public List ${getter}() {
        return getInstance() == null ? 
            null : new ArrayList( getInstance().${getter}() );
    }
</#if>
</#foreach>

}
