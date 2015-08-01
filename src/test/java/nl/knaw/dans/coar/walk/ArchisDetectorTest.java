package nl.knaw.dans.coar.walk;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class ArchisDetectorTest
{
    
    @Test
    public void testNummerPattern() {
        Pattern pat = Pattern.compile(".*?([a-zA-Z\\-]*[nummer|code\nr\\.][a-zA-Z\\-\\(\\)/:]*)[\\D]{1,10}([0-9\\.]{4,10}).*");
        String data = "bla een-nr.-(tje)/CIS-cod:   uuu   23.456 en meer";
        Matcher m = pat.matcher(data);
        if (m.matches()) {
            System.err.println(m.group(1));
            System.err.println(m.group(2));
        }
    }
    
    @Test
    public void testPattern() {
        Pattern pText = Pattern.compile(".*?([a-zA-Z0-9\\-]*\\s{0,1}[a-zA-Z\\-]*(nummer|code|nr\\.)[a-zA-Z\\-\\(\\)/:]*\\s{0,1}[a-zA-Z\\-\\(\\)/:]*)[\\D]{1,25}([0-9\\.]{4,10}).*?");
        String data = "Onderzoeksgegevens" +
                "Onderzoek: Onder de vloer van café Marktzicht (2005)" +
                "CIS-code / OM nr.: 9313" +
                "SIC: LVK 2A" +
                "Waarnemingsnummer: 402892" +
                "meer letters nr.: 5678 en verder";
        Matcher m = pText.matcher(data);
        while (m.find()) {
            System.err.println(m.group(1));
            //System.err.println(m.group(2));
            System.err.println(m.group(3));
        }
    }

}
