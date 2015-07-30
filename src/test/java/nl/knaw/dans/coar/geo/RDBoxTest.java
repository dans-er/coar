package nl.knaw.dans.coar.geo;

import static org.junit.Assert.*;

import org.junit.Test;

public class RDBoxTest
{
    
    @Test
    public void testConstructor() throws Exception {
        RDBox box = new RDBox(1, 2, 3, 4);
        assertEquals(1, box.getWestLimit());
        assertEquals(2, box.getEastLimit());
        assertEquals(3, box.getSouthLimit());
        assertEquals(4, box.getNorthLimit());
    }
    
    @Test
    public void getCenterPoint() throws Exception {
        RDBox box = new RDBox(399055, 111470, 399025, 111350);
        System.err.println(box.getCenterPoint().getGoogleMapsURL());
        System.err.println(box.getCenterPoint().getOpenStreetMapsSearchURL());
    }

}
