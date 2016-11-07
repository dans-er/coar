package nl.knaw.dans.coar.shed;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasetDiscriminator
{
    
    private static Logger logger = LoggerFactory.getLogger(DatasetDiscriminator.class);
    
    private List<String> datasetList;
    


    public boolean accept(String datasetId)
    {
        boolean contains = getDatasetList().contains(datasetId);
        if (contains) {
            logger.info("Not accepting dataset with datasetId {}", datasetId);
        }
        return !contains;
    }
    
    private List<String> getDatasetList() {
        if (datasetList == null) {
            File file = new File("list_datasets_excluded.txt");
            if (file.exists()) {
                FileInputStream fis = null;
                try
                {
                    fis = new FileInputStream(file);
                    datasetList = IOUtils.readLines(fis);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                } finally {
                    IOUtils.closeQuietly(fis);
                }
                
            } else {
                datasetList = Collections.emptyList();
            }
        }
        return datasetList;
    }

}
