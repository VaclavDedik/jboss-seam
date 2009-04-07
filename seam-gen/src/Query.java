package @actionPackage@;

import @modelPackage@.@entityName@;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("@componentName@")
public class @beanName@ extends EntityQuery<@entityName@>
{
    private static final String EJBQL = "@query@";
    private static final String[] RESTRICTIONS = {};

    public @beanName@()
    {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setMaxResults(25);
    }

    @RequestParameter
    @Override
    public void setFirstResult(Integer firstResult) {
        super.setFirstResult(firstResult);
    }
}
