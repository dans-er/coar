package nl.knaw.dans.coar.fedora;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import nl.knaw.dans.coar.geo.RDBox;
import nl.knaw.dans.coar.geo.RDPoint;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

public class EMDTest
{
    
    private Document readDoc(String file) throws JDOMException, IOException {
        SAXBuilder jdom = new SAXBuilder();
        return jdom.build("non-pub/test-files/" + file);
    }
    
    @Test
    public void evaluateFirstElement() throws Exception {
        EMD emd = new EMD(readDoc("emd.xml"));
        String xpath = "emd:easymetadata/emd:other/eas:application-specific/eas:notanelement";
        Element element = emd.evaluateFirstElement(xpath, EMD.NS_EMD, EMD.NS_EAS);
        assertNull(element);
    }
    
    @Test
    public void getFirstTitle() throws Exception {
        EMD emd = new EMD(readDoc("emd.xml"));
        assertEquals("Katwijk-Voorstraat 59", emd.getFirstTitle());
    }
    
    @Test
    public void getFirstPublisher() throws Exception {
        EMD emd = new EMD(readDoc("emd2.xml"));
        assertEquals("ADC ArcheoProjecten", emd.getFirstPublisher());
    }
    
    @Test
    public void getAccessRights() throws Exception {
        EMD emd = new EMD(readDoc("emd.xml"));
        assertEquals("GROUP_ACCESS", emd.getAccessRights());
    }
    
    @Test
    public void getFirstRightsHolder() throws Exception {
        EMD emd = new EMD(readDoc("emd.xml"));
        assertEquals("Archeologisch Onderzoek Leiden", emd.getFirstRightsHolder());
    }
    
    @Test
    public void getMetadataFormat() throws Exception {
        EMD emd = new EMD(readDoc("emd.xml"));
        assertEquals("ARCHAEOLOGY", emd.getMetadataFormat());
    }
    
    @Test
    public void getArchisVondstMelding() throws Exception {
        EMD emd = new EMD(readDoc("emd.xml"));
        assertEquals("422147", emd.getArchisVondstMelding());
    }
    
    @Test
    public void getArchisWaarneming() throws Exception {
        EMD emd = new EMD(readDoc("emd.xml"));
        assertEquals("437059", emd.getArchisWaarneming());
    }
    
    @Test
    public void getArchisOnderzoeksMeldingsNummer() throws Exception {
        EMD emd = new EMD(readDoc("emd.xml"));
        assertEquals("50931", emd.getArchisOnderzoeksMeldingsNummer());
    }
    
    @Test
    public void getDCMITypes() throws Exception {
        EMD emd = new EMD(readDoc("emd.xml"));
        List<String> types = emd.getDCMITypes();
        assertEquals(2, types.size());
        assertTrue(types.contains("Dataset"));
        assertTrue(types.contains("Image"));
    }
    
    @Test
    public void getRDPoints() throws Exception {
        EMD emd = new EMD(readDoc("emd2.xml"));
        List<RDPoint> points = emd.getSpatialPoints();
        assertEquals(2, points.size());  
        assertTrue(points.contains(new RDPoint(211229, 467610)));
        assertTrue(points.contains(new RDPoint(211537, 467435)));
    }
    
    @Test
    public void getRDBoxes() throws Exception {
        EMD emd = new EMD(readDoc("emd3.xml"));
        List<RDBox> boxes = emd.getSpatialBoxes();
        assertEquals(2, boxes.size());
        assertTrue(boxes.contains(new RDBox(399055, 111470, 399025, 111350)));
        assertTrue(boxes.contains(new RDBox(399000, 111400, 398000, 111300)));
    }

}
