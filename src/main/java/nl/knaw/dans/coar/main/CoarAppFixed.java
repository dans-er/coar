package nl.knaw.dans.coar.main;

import nl.knaw.dans.coar.fedora.DateBoundDatasetIterator;
import nl.knaw.dans.coar.fedora.FedoraConnector;
import nl.knaw.dans.coar.shed.Conveyor;
import nl.knaw.dans.coar.tika.TikaProcessor;
import nl.knaw.dans.coar.walk.ArchisDetector;
import nl.knaw.dans.coar.walk.CoordinateDetector;

public class CoarAppFixed
{

    public static void main(String[] args) throws Exception
    {
        FedoraConnector connector = new FedoraConnector();
        connector.connect();
        
        DateBoundDatasetIterator dbiter = new DateBoundDatasetIterator();
        TikaProcessor processor = new TikaProcessor();
        processor.addBodyHandler(new CoordinateDetector());
        processor.addBodyHandler(new ArchisDetector());
        
        Conveyor conveyor = new Conveyor(dbiter);
        conveyor.setPdfProcessor(processor);
        //conveyor.setSavingFiles(true);
        conveyor.run();

    }

}
