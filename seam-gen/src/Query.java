package @actionPackage@;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.framework.EntityQuery;

@Name("@componentName@")
public class @beanName@ extends EntityQuery
{
    @Override
    public String getEjbql() 
    { 
        return "@query@";
    }

    @RequestParameter
    @Override
    public void setFirstResult(Integer firstResult) {
        super.setFirstResult(firstResult);
    }
    
    @Override
    public Integer getMaxResults() {
        return 25;
    }
}

