package nl.knaw.dans.coar.geo;

import static org.junit.Assert.*;
import nl.knaw.dans.coar.geo.RDPoint;
import nl.knaw.dans.coar.geo.WGS84Point;

import org.junit.Test;

public class RDPointTest
{
    
    @Test
    public void testConstructor() throws Exception {
        RDPoint rdp = new RDPoint(2, 1);
        assertEquals(1, rdp.getX());
        assertEquals(2, rdp.getY());
        assertTrue(rdp.isXyExchanged());
    }
    
    @Test
    public void convert() throws Exception {
        WGS84Point wgs = RDPoint.convert(124188, 525251);
        assertEquals("52.713778,4.931245", wgs.toString());
        System.err.println(wgs.toString());       
    }
    
    @Test
    public void testURLs() {
        RDPoint rdp = new RDPoint(300000, 700000);
        System.err.println(rdp.getGoogleMapsURL());
        System.err.println(rdp.getOpenStreetMapsURL());
        System.err.println(rdp.getOpenStreetMapsSearchURL());
        System.err.println(rdp.getGeoNamesPostalCodeURL("demo"));
    }
    
    @Test
    public void testStringConstructor() throws Exception {
        RDPoint rdp = new RDPoint("124.188", "525.251");
        assertEquals(124188, rdp.getX());
        assertEquals(525251, rdp.getY());
    }

}
