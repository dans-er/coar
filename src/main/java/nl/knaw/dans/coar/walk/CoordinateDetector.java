package nl.knaw.dans.coar.walk;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.knaw.dans.coar.geo.RDPoint;
import nl.knaw.dans.coar.tika.Spatial;
import nl.knaw.dans.coar.tika.TikaBodyHandler;
import nl.knaw.dans.coar.tika.TikaProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CoordinateDetector extends DefaultHandler implements TikaBodyHandler 
{
    
    private static Logger logger = LoggerFactory.getLogger(CoordinateDetector.class);
    
    private TikaProcessor parentProcessor;
    private StringBuilder elementBuilder;
    
      
    // on all data lines
    private Pattern thePattern = Pattern.compile(".*?[Cc]o.{1,2}rd.*?(\\d{1,3}[\\.,]{0,1}\\d{3})[\\D]{1,10}(\\d{1,3}[\\.,]{0,1}\\d{3}).*?");
    private Pattern nozwPattern = Pattern.compile(".*?([Nn]oord|[Oo]ost|[Zz]uid|[Ww]est)[\\D]{1,20}(\\d{1,3}[\\.,]{0,1}\\d{3})[\\D]{1,10}(\\d{1,3}[\\.,]{0,1}\\d{3}).*?");
    private Pattern xyPattern = Pattern.compile(".*?[Xx]:[ ]{0,2}(\\d{1,3}[\\.,]{0,1}\\d{3})[\\D]*[Yy]:[ ]{0,2}(\\d{1,3}[\\.,]{0,1}\\d{3}).*?");
    
    // indicating patterns, if found look in next data lines for rdPattern 
    private Pattern coorPattern = Pattern.compile(".*[Cc][Oo].{1,2}[Rr][Dd][Ii][Nn][Aa].*");
    private Pattern hoekpuntenPattern = Pattern.compile(".*[hH][Oo][Ee][Kk][Pp][Uu][Nn][Tt].*");
    
    // a single coordinate
    private Pattern rdPattern = Pattern.compile("([0-9]{1,3})[\\.,]{0,1}([0-9]{3})");
    
    
    private String currentX;
    private int linesAfterPointAdded;
    private int linesAfterIndicator;
    private int linesAfterXFound;
    private int pointIndex;
    
    public CoordinateDetector() {
        
    }
    
    @Override
    public void setParentProcessor(TikaProcessor tikaProcessor)
    {
        parentProcessor = tikaProcessor;        
    }

    @Override
    public void startDocument() throws SAXException
    {
        linesAfterPointAdded = 100;
        linesAfterIndicator = 100;
        linesAfterXFound = 100;
        pointIndex = 0;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
    {
        elementBuilder = new StringBuilder();
        
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        String data = elementBuilder.toString();
        //System.err.println(data);

        if (!"".equals(data)) {
            linesAfterPointAdded++;
            linesAfterIndicator++;
            linesAfterXFound++;
        }
        if (linesAfterXFound > 3) { // a single coordinate was found, but after 3 lines forget about it.
            currentX = null;
        }
        boolean hasIndicator = hasIndicator(data);
        
        //System.err.println(parentProcessor.getPageCount() + " " + hasIndicator + " " + linesAfterIndicator + " " + linesAfterPointAdded + " "+ data);
        if (hasIndicator || linesAfterIndicator < 4 || linesAfterPointAdded < 4) {
            analyze(data);
        }
        tryPattern(data, thePattern, 1); // <== method 1
        tryPattern(data, xyPattern, 6);
        Matcher m = nozwPattern.matcher(data);
        while(m.find()) {
            String x = m.group(2);
            String y = m.group(3);
            logger.debug("Found {}/{} with {}", x, y, nozwPattern.toString());
            addPoint(x, y, data, 110);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        elementBuilder.append(ch, start, length);
        //System.err.println(elementBuilder.toString());
    }
    
    protected boolean hasIndicator(String data) {
        boolean hasIndicator = hasCoordinate(data) || hasHoekpunt(data);
        if (hasIndicator) linesAfterIndicator = 0;
        return hasIndicator;
    }

    protected boolean hasCoordinate(String data)
    {
        return coorPattern.matcher(data).matches();
    }
    
    protected boolean hasHoekpunt(String data) {
        boolean hoekpunt = hoekpuntenPattern.matcher(data).matches();
        return hoekpunt;
    }

    protected void analyze(String data) {
        
        boolean found = false;
        
        if (!found) {
            found = tryCummulative(data); // <== 2 and 3 
        }
        
    }
    
    public boolean tryPattern(String data, Pattern pat, int method) {
        boolean found = false;
        Matcher m = pat.matcher(data);
        while (m.find()) {
            found = true;
            String x = m.group(1);
            String y = m.group(2);
            logger.debug("Found {}/{} with {}", x, y, pat.toString());
            addPoint(x, y, data, method);
        }
        return found;
    }

    public boolean tryCummulative(String data)
    {
        boolean foundOne = false;
        Matcher m = rdPattern.matcher(data);
        int count = 0;
        String[] coordinates = new String[2];
        while (m.find()) {
            String coor = m.group();
            if (data.contains(":" + coor)) {
                logger.debug("Ignoring {}", ":" + coor);
                continue;
            }
            if (data.contains("-" + coor)) {
                logger.debug("Ignoring {}", ":" + coor);
                continue;
            }
            foundOne = true;
            coordinates[count] = coor;
            count++;
            //System.err.println(count + " " + coordinates[0] + "/" + coordinates[1]);
            if (count == 2) {
                count = 0;
                addPoint(coordinates[0], coordinates[1], data, 2); // <== method 2
                logger.debug("Found {}/{} with {}", coordinates[0], coordinates[1], rdPattern.toString());
                coordinates[0] = null; coordinates[1] = null;
            }
        }
        if (count == 1 && currentX != null) {
            String currentY = coordinates[0];
            addPoint(currentX, currentY, data, 3); // <== method 3
            logger.debug("Found in two stages {}/{}", currentX, currentY);
            currentX = null;
        } else if (count == 1 && currentX == null) {
            linesAfterXFound = 0;
            currentX = coordinates[0];
        }
        return foundOne;
    }

    public void addPoint(String x, String y, String data, int method)
    {
        RDPoint point = null;
        try {
            point = new RDPoint(x, y);
        } catch (Exception e) {
            logger.error("Could not create point. data=[" + data + "]", e);
            return;
        }
        if (!isPoint(point, data, method)) {
            return;
        }
        pointIndex++;
        //if (pointIndex == 4) System.err.println(parentProcessor.getPageCount()  + " " + linesAfterIndicator + " " + linesAfterPointAdded + " "+ data);
        try
        {
            String src = "page:" + parentProcessor.getPageCount();
            Spatial spatial = new Spatial(src, point);
            spatial.setSpatialType("pdf");
            spatial.setPointIndex(pointIndex);
            spatial.setMethod(method);
            parentProcessor.getCurrentProfile().addSpatial(spatial);
            linesAfterPointAdded = 0;
            
        }
        catch (Exception e)
        {
            logger.error("Could not add spatial. data=[" + data + "]", e);
        }
    }

    protected boolean isPoint(RDPoint point, String data, int method)
    {
        if (point.getX() < 10000) {
            logger.debug("No point. x < 10.000");
            return false;
        }
        if (point.getX() > 300000) {
            logger.debug("No point. x > 300.000");
            return false;
        }
        if (point.getY() < 300000) {
            logger.debug("No point. y < 300.000");
            return false;
        }
        if (point.getY() > 700000) {
            logger.debug("No point. y > 700.000");
            return false;
        }
        return true;
    }

}
