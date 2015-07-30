package nl.knaw.dans.coar.walk;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.knaw.dans.coar.tika.ArchisNummer;
import nl.knaw.dans.coar.tika.TikaBodyHandler;
import nl.knaw.dans.coar.tika.TikaProcessor;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ArchisDetector extends DefaultHandler implements TikaBodyHandler
{
    
    public static final int KEEP_LINES = 3;
    
    //private static Logger logger = LoggerFactory.getLogger(ArchisDetector.class);
    
    private TikaProcessor parentProcessor;
    private StringBuilder elementBuilder;
    private int lineCount;
    private int nummerIndex;
    private List<Indicator> indicatorList = new ArrayList<Indicator>();
    private List<String> negKeyList = new ArrayList<String>();
    private List<String> negDataList = new ArrayList<String>();
    
    private Pattern pDirectNumber = Pattern.compile(".*?([a-zA-Z\\-]*nummer[a-zA-Z\\-\\(\\)/:]*\\s{0,1}[a-zA-Z\\-\\(\\)/:]*)[\\D]{1,25}([0-9\\.]{4,10}).*?");
    private Pattern pDirectCode = Pattern.compile(".*?([a-zA-Z\\-]*code[a-zA-Z\\-\\(\\)/:]*\\s{0,1}[a-zA-Z\\-\\(\\)/:]*)[\\D]{1,25}([0-9\\.]{4,10}).*?");
    private Pattern pKeyOnly = Pattern.compile("([a-zA-Z\\-]*nummer[a-zA-Z\\-\\(\\)/:]*)");
    private Pattern pNummer = Pattern.compile("([0-9\\.]{4,15})");

    public ArchisDetector()
    {
        negKeyList.add("code");
        
        negKeyList.add("fotonummer");
        negKeyList.add("fotonummers");
        
        negKeyList.add("genummerd");
        negKeyList.add("genummerde");
        
        negKeyList.add("nummer");
        negKeyList.add("nummers");
        
        negKeyList.add("boornummer");
        negKeyList.add("boornummers");

        negKeyList.add("laagnummer");
        negKeyList.add("laagnummers");
        
        negKeyList.add("perceel-nummer");
        negKeyList.add("perceel-nummers");
        negKeyList.add("perceelnummer");
        negKeyList.add("perceelnummers");
        negKeyList.add("perceelsnummer");
        negKeyList.add("perceelsnummers");
        
        negKeyList.add("putnummer");
        negKeyList.add("putnummers");
        
        negKeyList.add("spoornummer");
        negKeyList.add("spoornummers");
        negKeyList.add("sporennummer");
        negKeyList.add("sporennummers");
        
        negKeyList.add("versienummer");
        negKeyList.add("versienummers");
        
        negKeyList.add("vondstnummer");
        negKeyList.add("vondstnummers");

        negKeyList.add("weeknummer");
        negKeyList.add("weeknummers");
        
        negDataList.add("niet van toepassing");
    }
    

    @Override
    public void startDocument() throws SAXException
    {
        lineCount = 0;
        nummerIndex = 0;
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
        if (StringUtils.isNotBlank(data)) {
            lineCount++;
            removeOldIndicators();
        }
        analyze(data);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        elementBuilder.append(ch, start, length);
    }
    
    protected void analyze(String data) {
        boolean found = false;
        
        Matcher m = pDirectNumber.matcher(data);
        while (m.find()) {
            found = true;
            String key = m.group(1);
            String value = m.group(2);
            addNummer(key, value, data, 1); // <== method 1
        }

        m = pDirectCode.matcher(data);
        while (m.find()) {
            found = true;
            String key = m.group(1);
            String value = m.group(2);
            addNummer(key, value, data, 2); // <== method 2
        }

        String dataLow = data.toLowerCase();
        for (String neg : negDataList) {
            if (dataLow.contains(neg)) return;
        }
        
        if (!found) {
            m = pKeyOnly.matcher(data);
            while (m.find()) {
                String key = m.group();
                indicatorList.add(new Indicator(key, lineCount, parentProcessor.getPageCount()));
            }
        }
        if (!found && !indicatorList.isEmpty()) {
            m = pNummer.matcher(data);
            while (m.find() && !indicatorList.isEmpty()) {
                String value = m.group();
                Indicator ind = indicatorList.remove(0);
                addNummer(ind.getIndicatorKey(), value, data, 3); // <== method 3
            }
        }
    }
    

    private void addNummer(String key, String value, String data, int method)
    {
        if (negKeyList.contains(key.toLowerCase().replaceAll(":", "").trim())) {
            return;
        }
        nummerIndex++;
        ArchisNummer an = new ArchisNummer();
        an.setKey(key);
        an.setValue(value);
        an.setSource("page:" + parentProcessor.getPageCount());
        an.setNummerIndex(nummerIndex);
        an.setMethod(method);
        parentProcessor.getCurrentProfile().addArchisNummer(an);
    }

    @Override
    public void setParentProcessor(TikaProcessor tikaProcessor)
    {
        parentProcessor = tikaProcessor;
    }
    
    private void removeOldIndicators()
    {
        List<Indicator> removables = new ArrayList<Indicator>();
        for (Indicator ind : indicatorList) {
            if ((ind.getLine() + KEEP_LINES) < lineCount) {
                removables.add(ind);
            }
        }
        for (Indicator removable : removables) {
            indicatorList.remove(removable);
        }
        
    }


}
