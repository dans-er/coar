package nl.knaw.dans.coar.fedora;

import java.util.List;

import org.junit.Test;

import com.yourmediashelf.fedora.client.request.DescribeRepository;
import com.yourmediashelf.fedora.client.response.DescribeRepositoryResponse;
import com.yourmediashelf.fedora.generated.access.FedoraRepository;

public class FedoraConnectorTest
{
       @Test
       public void testConnection() throws Exception {
           FedoraConnector fcon = new FedoraConnector();
           fcon.connect();
           
           DescribeRepositoryResponse response = new DescribeRepository().execute();
           FedoraRepository frepo = response.getRepositoryInfo();
           System.err.println(frepo.getRepositoryName());
           List<String> adminEmails = frepo.getAdminEmail();
           for (String email : adminEmails)
           {
               System.err.println("admin-email: " + email);
           }
           System.err.println(frepo.getRepositoryVersion());
           System.err.println(frepo.getRepositoryBaseURL());
           System.err.println(frepo.getSampleAccessURL());
           System.err.println(frepo.getRepositoryPID().getPIDSample());
       }
}
