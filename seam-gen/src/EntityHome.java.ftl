${pojo.packageDeclaration}
<#assign classbody>
<#assign entityName = pojo.shortName>
<#assign componentName = util.lower(entityName)>
<#assign homeName = componentName + "Home">
@${pojo.importType("org.jboss.seam.annotations.Name")}("${homeName}")
public class ${entityName}Home extends ${pojo.importType("org.jboss.seam.framework.EntityHome")}<${entityName}>
{

<#foreach property in pojo.allPropertiesIterator>
<#if c2h.isManyToOne(property)>
<#assign parentPojo = c2j.getPOJOClass(cfg.getClassMapping(property.value.referencedEntityName))>
<#assign parentHomeName = util.lower(parentPojo.shortName) + "Home">
    @${pojo.importType("org.jboss.seam.annotations.In")}(value="${'#'}{${parentHomeName}.instance}", required=false)
    ${parentPojo.shortName} ${property.name};
</#if>
</#foreach>

<#assign idName = entityName + util.upper(pojo.identifierProperty.name)>
<#assign idType = pojo.importType(pojo.identifierProperty.type.returnedClass.name)>
    public void set${idName}(${idType} id)
    {
        setId(id);
    }

    public ${idType} get${idName}()
    {
        return (${idType}) getId();
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
<#assign childPojo = c2j.getPOJOClass(property.value.element.associatedClass)>
    public ${pojo.importType("java.util.List")}<${childPojo.shortName}> ${getter}() {
        return getInstance() == null ? 
            null : new ${pojo.importType("java.util.ArrayList")}<${childPojo.shortName}>( getInstance().${getter}() );
    }
</#if>
</#foreach>

}
</#assign>

${pojo.generateImports()}
${classbody}
