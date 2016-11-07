package nl.knaw.dans.coar.fedora;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class PerDatasetIteratorTest extends AbstractFedoraTest
{
    
    @Test
    public void testNext() {
        int count = 0;
        PerDateDatasetIterator pdi = new PerDateDatasetIterator();
        pdi.setStartDate("2005-01-01");
        while (pdi.hasNext()) {
            count++;
            pdi.next();
        }
        System.err.println(count);
    }
    

}
