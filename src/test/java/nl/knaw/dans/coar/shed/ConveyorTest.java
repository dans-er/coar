package nl.knaw.dans.coar.shed;

import nl.knaw.dans.coar.fedora.AbstractFedoraTest;
import nl.knaw.dans.coar.tika.TikaProcessor;
import nl.knaw.dans.coar.walk.ArchisDetector;
import nl.knaw.dans.coar.walk.CoordinateDetector;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ConveyorTest extends AbstractFedoraTest
{
    
    @Test
    public void testOneDataset() throws Exception {
        String datasetId = "easy-dataset:22110";
        TikaProcessor tp = new TikaProcessor();
        tp.addBodyHandler(new CoordinateDetector());
        tp.addBodyHandler(new ArchisDetector());
        
        Conveyor conveyor = new Conveyor(null);
        conveyor.setPdfProcessor(tp);
        conveyor.processDataset(datasetId);
    }

}
