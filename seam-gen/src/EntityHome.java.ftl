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
    @${pojo.importType("org.jboss.seam.annotations.In")}(create=true)
    ${parentPojo.shortName}Home ${parentHomeName};
</#if>
</#foreach>

<#assign idName = entityName + util.upper(pojo.identifierProperty.name)>
<#if c2j.isComponent(pojo.identifierProperty)>
<#assign idType = entityName + "Id">
<#else>
<#assign idType = pojo.importType(pojo.identifierProperty.type.returnedClass.name)>
</#if>
    public void set${idName}(${idType} id)
    {
        setId(id);
    }

    public ${idType} get${idName}()
    {
        return (${idType}) getId();
    }

<#if pojo.isComponent(pojo.identifierProperty)>
    public ${entityName}Home()
    {
        set${idName}( new ${entityName}Id() );
    }
    
    @Override
    public boolean isIdDefined()
    {
<#foreach property in pojo.identifierProperty.value.propertyIterator>
<#assign getter = pojo.getGetterSignature(property)>
        if ( get${idName}().${getter}()==null ) return false;
</#foreach>
        return true;
    }

</#if>
    @Override
    protected ${entityName} createInstance()
    {
        ${entityName} ${componentName} = new ${entityName}();
<#if pojo.isComponent(pojo.identifierProperty)>
        ${componentName}.setId( new ${entityName}Id() );
</#if>
        return ${componentName};
    }
    
    public void wire()
    {
<#foreach property in pojo.allPropertiesIterator>
<#if c2h.isManyToOne(property)>
<#assign parentPojo = c2j.getPOJOClass(cfg.getClassMapping(property.value.referencedEntityName))>
<#if parentPojo.shortName!=pojo.shortName>
<#assign parentHomeName = util.lower(parentPojo.shortName) + "Home">
<#assign setter = "set" + pojo.getPropertyName(property)>
        ${parentPojo.shortName} ${property.name}=${parentHomeName}.getDefinedInstance();
        if ( ${property.name}!=null )
        {
           getInstance().${setter}(${property.name});
        }
</#if>
</#if>
</#foreach>
    }
    
    public boolean isWired()
    {
<#foreach property in pojo.allPropertiesIterator>
<#if (c2h.isManyToOne(property) && !property.optional)>
<#assign getter = pojo.getGetterSignature(property)>
        if ( getInstance().${getter}()==null ) return false;
</#if>
</#foreach>
        return true;
    }
    
    public ${entityName} getDefinedInstance()
    {
        return isIdDefined() ? getInstance() : null;
    }
 	
<#foreach property in pojo.allPropertiesIterator>
<#assign getter = pojo.getGetterSignature(property)>
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
