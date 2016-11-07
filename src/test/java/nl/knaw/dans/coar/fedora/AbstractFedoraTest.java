package nl.knaw.dans.coar.fedora;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.request.DescribeRepository;
import com.yourmediashelf.fedora.client.request.FedoraRequest;
import com.yourmediashelf.fedora.client.response.DescribeRepositoryResponse;

public abstract class AbstractFedoraTest
{
    
    public static final String FEDORA_VERSION = "3.5";
    
    private static Logger logger = LoggerFactory.getLogger(AbstractFedoraTest.class);
    
    private static final String props_location = "non-pub/remote_testing.properties";
    private static Properties props;
    
        
    
    public static boolean remoteTestingEnabled() {
        File file = new File(props_location);
        return file.exists();
    }
    
    public static Properties getProps()
    {
        try
        {
            if (props == null) {
                props = new Properties();
                InputStream ins = FileUtils.openInputStream(new File(props_location));
                props.load(ins);
                ins.close();
            }
            return props;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    
    @BeforeClass
    public static void connectionTest() throws Exception {
        if (remoteTestingEnabled()) {
            String fedoraAddress = getProps().getProperty("fedora.host") + ":" //
                    + getProps().getProperty("fedora.port") + "/" //
                    + getProps().getProperty("fedora.path");
            FedoraCredentials credentials = new FedoraCredentials(
                    fedoraAddress, 
                    getProps().getProperty("fedora.user"), 
                    getProps().getProperty("fedora.pass"));
            FedoraClient fedora = new FedoraClient(credentials);
            FedoraRequest.setDefaultClient(fedora);
            
            try
            {
                DescribeRepositoryResponse response = new DescribeRepository().execute();
                String version = response.getRepositoryVersion();
                assertEquals(FEDORA_VERSION, version);
                System.out.println("Connection tested");
            }
            catch (Exception e)
            {
                logger.error("Could not connect to Fedora: ", e);
                throw e;
            }
        } else {
            System.err.println("Remote testing not enabled.");
        }
    }



}
