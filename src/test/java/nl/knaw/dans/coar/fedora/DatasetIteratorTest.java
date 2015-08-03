package nl.knaw.dans.coar.fedora;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.yourmediashelf.fedora.generated.management.DatastreamProfile;

@Ignore
public class DatasetIteratorTest extends AbstractFedoraTest
{
    
    @Test
    public void tryFirst() throws Exception {
        DatasetIterator iter = new DatasetIterator();
        int count = 0;
        Fedora fedora = Fedora.instance();
        while (iter.hasNext() && count < 100) {
            String datasetId = iter.next();
            count++;
            EMD emd = new EMD(fedora.getEMD(datasetId));
            
            System.err.println(count + " " + datasetId + " " + emd.getMetadataFormat());
            if ("ARCHAEOLOGY".equals(emd.getMetadataFormat())) {
                List<String> fileIds = fedora.getFileIdentifiers(datasetId);
                for (String fileId : fileIds) {
                    DatastreamProfile dsProfile = fedora.getDSProfile(fileId);
                    
                    System.err.println("\t" + fileId + " " + dsProfile.getDsMIME() + " " + dsProfile.getDsLabel());
                    // ds_mediatype should be 'application/pdf'
                }
            }
            
        }
    }

}
