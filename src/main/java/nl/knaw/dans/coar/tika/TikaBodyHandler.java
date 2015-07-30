package nl.knaw.dans.coar.tika;

import org.xml.sax.ContentHandler;

public interface TikaBodyHandler extends ContentHandler
{
    
    void setParentProcessor(TikaProcessor tikaProcessor);
    
    

}
