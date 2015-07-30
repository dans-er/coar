package nl.knaw.dans.coar.shed;

import java.io.InputStream;

import nl.knaw.dans.coar.tika.TikaProfile;

public interface PDFProcessor
{
    
    void process(TikaProfile profile, InputStream data) throws Exception;

}
