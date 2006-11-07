<#assign entityName = pojo.shortName>
<#assign componentName = util.lower(entityName)>
<#assign homeName = componentName + "Home">
${pojo.packageDeclaration}

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.framework.EntityHome;

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
    
    @Override @Begin
    public void create() {
        super.create();
    }
 	
}
