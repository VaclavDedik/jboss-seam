package ${packageName};

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.framework.EntityHome;

import ${entityPackage}.${actionName};

@Name("${componentName}Home")
public class ${actionName}Home extends EntityHome<${actionName}>
{

    @RequestParameter 
    Long ${componentName}Id;
    
    @Override
    public Object getId() 
    { 
        return ${componentName}Id; 
    }
 	
}
