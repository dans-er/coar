package nl.knaw.dans.coar.walk;

import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.knaw.dans.coar.tika.TikaProcessor;
import nl.knaw.dans.coar.tika.TikaProfile;

import org.junit.Test;

public class CoordinateDetectorTest
{
    
    @Test
    public void isCoordinate() throws Exception
    {
        CoordinateDetector detector = new CoordinateDetector();
        assertTrue(detector.hasCoordinate("Coördinaten centrum plangebied X: 174.827 / Y: 421.460"));
        assertTrue(detector.hasCoordinate("bla coördinaten centrum plangebied X: 174.827 / Y: 421.460"));
        assertTrue(detector.hasCoordinate("bla cOörDiNaten centrum plangebied X: 174.827 / Y: 421.460"));
        
        assertTrue(detector.hasCoordinate("Project Opheusden ABC-terrein X-coordinaat 172.101 Boornummer 1"));
        assertTrue(detector.hasHoekpunt("Centrum Hoekpunten bla bla"));
        
        assertTrue(detector.hasCoordinate("257513 / 594980Coördinaten:"));
    }
    
    @Test
    public void testAnalyze() throws Exception {
        CoordinateDetector detector = new CoordinateDetector();
        TikaProcessor tp = new TikaProcessor();
        tp.setCurrentProfile(new TikaProfile(null, null));
        detector.setParentProcessor(tp);
        detector.analyze("boring: BOWZ-7beschrijver: AXM/CLS, datum: 17-6-2005, X: 111.320, Y: 455.782, boortype: Edelman-7 en guts-3 cm, doel boring: archeologie - kartering, landgebruik: verhard, vondstzichtbaarheid: slecht, provincie: Zuid-Holland, gemeente: Bodegraven, plaatsnaam: Bodegraven, opdrachtgever: Gemeente Bodegraven, uitvoerder: RAAP West");
    }
    
    @Test
    public void getPointless() {
        // coördinaten: 91000, 466000
        //String data = "OegstgeestToponiem: RijnfrontCentrum coördinaten: 91000, 466000bla";
        //String data = "OegstgeestToponiem: RijnfrontCentrum coördinaten: 91000, 46bla";
        String data= "beschrijver: AXM/CLS, datum: 17-6-2005, X: 111.320, Y: 455.782, boortype: Edelman-7 en guts-3 cm, doel boring: archeologie - kartering, landgebruik: verhard,"; 
        //Pattern pat = Pattern.compile(".*?[Cc]oördinaten:[ ]*(\\d{4,6})[\\D]*(\\d{4,6}).*");
        Pattern pat = Pattern.compile(".*?[Xx]:[ ]{0,2}(\\d{1,3}\\.\\d{3})[\\D]*[Yy]:[ ]{0,2}(\\d{1,3}\\.\\d{3}).*");
        Matcher m = pat.matcher(data);
        System.err.println(m.matches());
        System.err.println(m.groupCount());
        if (m.matches()) {
            System.err.println(m.group(1));
            System.err.println(m.group(2));
        }
        while (m.find()) {
            System.err.println(m.group());
        }
    }
    
    @Test
    public void upsidedown() {
        
        String data = "Houtwiel-West " +
                "194134/584422 194972/584799 " +
                "194747/585183 194048/584648";
        Pattern pat = Pattern.compile("([0-9]{1,3})[\\.,]{0,1}([0-9]{3})");
        Matcher m = pat.matcher(data);
        while(m.find()) {
            System.err.println(m.group());
//            System.err.println(m.group(2));
//            System.err.println(m.group(3));
        }
    }
    
}
