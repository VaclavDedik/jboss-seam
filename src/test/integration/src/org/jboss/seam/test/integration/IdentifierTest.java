package org.jboss.seam.test.integration;

import javax.persistence.EntityManager;

import org.drools.lang.DRLParser.identifier_return;
import org.jboss.seam.framework.EntityIdentifier;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

/**
 * @author Pete Muir
 *
 */
public class IdentifierTest extends SeamTest
{

    @Test
    public void testEntityIdentifier() throws Exception
    {
        new ComponentTest()
        {

            @Override
            protected void testComponents() throws Exception
            {
                setValue("#{countryHome.instance.name}", "foo");
                invokeMethod("#{countryHome.persist}");
                Country country = (Country) getValue("#{countryHome.instance}");
                EntityManager entityManager = (EntityManager) getValue("#{countryHome.entityManager}");
                
                EntityIdentifier entityIdentifier = new EntityIdentifier(country, entityManager);
                assert "foo".equals(((Country) entityIdentifier.find(entityManager)).getName());
                EntityIdentifier entityIdentifier2 = new EntityIdentifier(country, entityManager);
                assert entityIdentifier.equals(entityIdentifier2);
            }
            
        }.run();
    }
    
}
