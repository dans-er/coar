package nl.knaw.dans.coar.fedora;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Ignore;
import org.junit.Test;

import com.yourmediashelf.fedora.client.request.FindObjects;
import com.yourmediashelf.fedora.client.response.FindObjectsResponse;
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
    
    @Test
    public void testCount() throws Exception {
        Fedora fedora = Fedora.instance();
        DateTime start = new DateTime("2011-04-07");
        DateTime end = start.plusDays(1);
        String query = new StringBuilder() //
            .append("pid%7Eeasy-dataset:*").append(" ")
            .append("cDate%3E%3D").append(getFormat().print(start)).append(" ")
            .append("cDate%3C").append(getFormat().print(end))
            .toString();
        
        System.err.println(query);
        FindObjectsResponse response = new FindObjects() //
        .query(query) //
        .pid() //
        .maxResults(100000) //
        .execute();
        List<String> pids = response.getPids();
        System.err.println(pids.size());
    }
    
    private DateTimeFormatter getFormat() {
        return DateTimeFormat.forPattern("yyyy-MM-dd");
    }

}
