package nl.knaw.dans.coar.tika;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.coar.shed.PDFProcessor;

import org.apache.commons.lang.StringUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TikaProcessor extends DefaultHandler implements PDFProcessor
{

    public static final int MAX_CONTENT_LENGTH = 255;

    //private static Logger logger = LoggerFactory.getLogger(TikaProcessor.class);

    private final AutoDetectParser parser = new AutoDetectParser();
    
    
    private TikaProfile currentProfile;
    private boolean inBody;
    private int pageCount;
    
    private List<TikaBodyHandler> bodyHandlers = new ArrayList<TikaBodyHandler>();

    private static boolean isMeta(String uri, String localName)
    {
        return "http://www.w3.org/1999/xhtml".equals(uri) && "meta".equals(localName);
    }
    
    public static boolean isBody(String uri, String localName) {
        return "http://www.w3.org/1999/xhtml".equals(uri) && "body".equals(localName);
    }

    public TikaProcessor()
    {
        
    }
    
    public void addBodyHandler(TikaBodyHandler handler)
    {
        handler.setParentProcessor(this);
        bodyHandlers.add(handler);
    }

    public List<TikaBodyHandler> getBodyHandlers()
    {
        return bodyHandlers;
    }

    public void setBodyHandlers(List<TikaBodyHandler> bodyHandlers)
    {
        this.bodyHandlers = bodyHandlers;
        for (TikaBodyHandler handler : this.bodyHandlers) {
            handler.setParentProcessor(this);
        }
    }

    public TikaProfile getCurrentProfile()
    {
        return currentProfile;
    }

    public void setCurrentProfile(TikaProfile currentProfile)
    {
        this.currentProfile = currentProfile;
    }

    public int getPageCount()
    {
        return pageCount;
    }

    public void process(TikaProfile profile, InputStream data) throws IOException, SAXException,
            TikaException
    {
        
        currentProfile = profile;
        pageCount = 0;
        Metadata metadata = new Metadata();
        metadata.set(Metadata.RESOURCE_NAME_KEY, currentProfile.getDsLabel());

        ParseContext context = new ParseContext();

        parser.parse(data, this, metadata, context);

    }
    
    @Override
    public void startDocument() throws SAXException
    {
        for (TikaBodyHandler handler : bodyHandlers) {
            handler.startDocument();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
    {
        if (isBody(uri, localName)) {
            inBody = true;
        }
        try
        {
            if (isMeta(uri, localName))
            {
                String name = atts.getValue("name");
                String content = StringUtils.abbreviate(atts.getValue("content"), MAX_CONTENT_LENGTH);
                currentProfile.addMeta(new TikaMeta(name, content));
                if ("Content-Type".equals(name))
                {
                    currentProfile.setContentType(content);
                }
            }
        }
        catch (Exception e)
        {
            throw e;
        }
        if (inBody) {
            if ("http://www.w3.org/1999/xhtml".equals(uri) && "div".equals(localName)) {
                if ("page".equals(atts.getValue("class"))) {
                    pageCount++;
                }
            }
            for (TikaBodyHandler handler : bodyHandlers) {
                handler.startElement(uri, localName, qName, atts);
            }
        }
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        if (inBody) {
            for (TikaBodyHandler handler : bodyHandlers) {
                handler.endElement(uri, localName, qName);
            }
        }
        if (isBody(uri, localName)) {
            inBody = false;
        }
    }

    @Override
    public void endDocument() throws SAXException
    {
        for (TikaBodyHandler handler : bodyHandlers) {
            handler.endDocument();
        }

    }
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        if (inBody) {
            for (TikaBodyHandler handler : bodyHandlers) {
                handler.characters(ch, start, length);
            }
        }
    }
    

}
