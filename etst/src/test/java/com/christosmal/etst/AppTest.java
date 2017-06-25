package com.christosmal.etst;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testOrigUrl() {
        assertTrue (App.main("https://github.com/egis/handbook/blob/master/Tech-Stack.md") == 0 );
    }
    
    public void testInvalidUrl() {
        assertTrue(App.main("someInvalidUrl") == -1 );
    }
    
    public void testIncorrectUrl() {
        assertTrue(App.main("http://www.google.com") == -1 );
    }
    
    
}
