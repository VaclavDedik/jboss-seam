<#assign entityName = pojo.shortName>
<#assign componentName = util.lower(entityName)>
<#assign homeName = componentName + "Home">
${pojo.packageDeclaration}

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.framework.EntityHome;

import java.util.List;
import java.util.ArrayList;

@Name("${homeName}")
public class ${entityName}Home extends EntityHome<${entityName}>
{

    @RequestParameter 
    ${pojo.identifierProperty.type.returnedClass.name} ${componentName}Id;
    
<#foreach property in pojo.allPropertiesIterator>
<#if c2h.isManyToOne(property)>
<#assign parentPojo = c2j.getPOJOClass(cfg.getClassMapping(property.value.referencedEntityName))>
<#assign parentHomeName = util.lower(parentPojo.shortName) + "Home">
    @In(value="${'#'}{${parentHomeName}.instance}", required=false)
    ${parentPojo.shortName} ${property.name};
</#if>
</#foreach>

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
    
    @Override
    protected ${entityName} createInstance()
    {
        ${entityName} result = new ${entityName}();
<#foreach property in pojo.allPropertiesIterator>
<#if c2h.isManyToOne(property)>
<#assign setter = "set" + pojo.getPropertyName(property)>
        result.${setter}(${property.name});
</#if>
</#foreach>
        return result;
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
