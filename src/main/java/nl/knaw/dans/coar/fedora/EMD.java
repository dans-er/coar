package nl.knaw.dans.coar.fedora;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.coar.geo.RDBox;
import nl.knaw.dans.coar.geo.RDPoint;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;


public class EMD
{
    
    private final static XPathFactory xFactory = XPathFactory.instance();
    protected final static Namespace NS_EMD = Namespace.getNamespace("emd", "http://easy.dans.knaw.nl/easy/easymetadata/");
    protected final static Namespace NS_EAS = Namespace.getNamespace("eas", "http://easy.dans.knaw.nl/easy/easymetadata/eas/");
    protected final static Namespace NS_DC = Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/");
    protected final static Namespace NS_DCT = Namespace.getNamespace("dct", "http://purl.org/dc/terms/");
    
    private final Document doc;
    
    public EMD(Document doc) {
        this.doc = doc;
    }
    
    public String getFirstTitle() {
        String xpath = "/emd:easymetadata/emd:title/dc:title";
        return getFirstElementText(xpath);
    }
    
    public String getFirstRightsHolder() {
        String xpath = "/emd:easymetadata/emd:rights/dct:rightsHolder";
        return getFirstElementText(xpath);
    }
    
    public String getMetadataFormat() {
        String xpath = "/emd:easymetadata/emd:other/eas:application-specific/eas:metadataformat";
        return getFirstElementText(xpath);
    }
    
    public String getArchisVondstMelding() {
        String xpath = "/emd:easymetadata/emd:identifier/dc:identifier[@eas:scheme='Archis_vondstmelding']";
        return getFirstElementText(xpath);
    }
    
    public String getArchisWaarneming() {
        String xpath = "/emd:easymetadata/emd:identifier/dc:identifier[@eas:scheme='Archis_waarneming']";
        return getFirstElementText(xpath);
    }
    
    public String getArchisOnderzoeksMeldingsNummer() {
        String xpath = "/emd:easymetadata/emd:identifier/dc:identifier[@eas:scheme='Archis_onderzoek_m_nr']";
        return getFirstElementText(xpath);
    }
    
    public List<String> getDCMITypes() {
        String xpath = "/emd:easymetadata/emd:type/dc:type[@eas:scheme='DCMI']";
        return getElementValues(xpath);
    }
    
    public List<RDPoint> getSpatialPoints() {
        String xpath = "/emd:easymetadata/emd:coverage/eas:spatial/eas:point[@eas:scheme='RD']";
        List<Element> elements = evaluate(xpath);
        List<RDPoint> points = new ArrayList<RDPoint>();
        for (Element element : elements) {
            String x = element.getChild("x", NS_EAS).getTextNormalize();
            String y = element.getChild("y", NS_EAS).getTextNormalize();
            points.add(new RDPoint(x, y));
        }
        return points;
    }
    
    public List<RDBox> getSpatialBoxes() {
        String xpath = "/emd:easymetadata/emd:coverage/eas:spatial/eas:box[@eas:scheme='RD']";
        List<Element> elements = evaluate(xpath);
        List<RDBox> boxes = new ArrayList<RDBox>();
        for (Element element : elements) {
            String north = element.getChild("north", NS_EAS).getTextNormalize();
            String east = element.getChild("east", NS_EAS).getTextNormalize();
            String south = element.getChild("south", NS_EAS).getTextNormalize();
            String west = element.getChild("west", NS_EAS).getTextNormalize();
            boxes.add(new RDBox(north, east, south, west));
        }
        return boxes;
    }

    protected List<String> getElementValues(String xpath)
    {
        List<Element> elements = evaluate(xpath);
        List<String> values = new ArrayList<String>();
        for (Element element : elements) {
            values.add(element.getTextNormalize());
        }
        return values;
    }

    protected String getFirstElementText(String xpath)
    {
        Element element = evaluateFirstElement(xpath);
        if (element != null) {
            return element.getTextNormalize();
        } else {
            return null;
        }
    }
    
    protected Element evaluateFirstElement(String xpath, Namespace... namespaces) {
        if (namespaces == null | namespaces.length == 0) {
            namespaces = new Namespace[]{NS_EMD, NS_EAS, NS_DC, NS_DCT};
        }
        XPathExpression<Element> expr = xFactory.compile(
                xpath, Filters.element(), null, namespaces);
        return expr.evaluateFirst(doc);
    }
    
    protected List<Element> evaluate(String xpath, Namespace... namespaces) {
        if (namespaces == null | namespaces.length == 0) {
            namespaces = new Namespace[]{NS_EMD, NS_EAS, NS_DC, NS_DCT};
        }
        XPathExpression<Element> expr = xFactory.compile(
                xpath, Filters.element(), null, namespaces);
        return expr.evaluate(doc);
    }
    

}
