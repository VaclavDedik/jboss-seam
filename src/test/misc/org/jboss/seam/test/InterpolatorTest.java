package org.jboss.seam.test;

import java.util.Date;

import org.jboss.seam.core.Interpolator;
import org.testng.Assert;
import org.testng.annotations.Test;


public class InterpolatorTest
{
    
    static final String CHOICE_EXPR = "There {0,choice,0#are no files|1#is one file|1<are {0,number,integer} files}.";
    @Test
    public void testFail() 
    {
        Interpolator interpolator = Interpolator.instance();

        Assert.assertEquals("3 5 7", interpolator.interpolate("#0 #1 #2", 3, 5, 7));
        Assert.assertEquals("3 5 7", interpolator.interpolate("{0} {1} {2}", 3, 5, 7));

        // this tests that the result of an expression evaluation is not evaluated again
        Assert.assertEquals("{0}", interpolator.interpolate("{1}", "bad", "{0}"));
        
        // this tests that embedded {} expressions are parsed correctly.
        Assert.assertEquals("There are no files.", interpolator.interpolate(CHOICE_EXPR, 0));
        Assert.assertEquals("There is one file.", interpolator.interpolate(CHOICE_EXPR, 1));
        Assert.assertEquals("There are 2 files.", interpolator.interpolate(CHOICE_EXPR, 2));
                   
        Assert.assertEquals("12/31/69", interpolator.interpolate("{0,date,short}", new Date(0)));
 
        
        // test that a messageformat error doesn't blow up
        Assert.assertEquals("{nosuchmessage}", interpolator.interpolate("{nosuchmessage}"));
    }

}
