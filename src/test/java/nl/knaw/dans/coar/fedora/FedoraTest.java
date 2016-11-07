package nl.knaw.dans.coar.fedora;


import java.util.List;

import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class FedoraTest extends AbstractFedoraTest
{
    
    @Test
    public void getEMD() throws Exception {
        Fedora fedora = Fedora.instance();
        Document doc = fedora.getEMD("easy-dataset:4705");
        System.err.println(doc.toString());
        System.err.println(new XMLOutputter().outputString(doc));
    }
    
    @Test
    public void getFileIdentifiers() throws Exception {
        Fedora fedora = Fedora.instance();
        List<String> lines = fedora.getFileIdentifiers("easy-dataset:4382");
        for (String line : lines) {
            System.err.println(line);
        }
    }
    
   

}
